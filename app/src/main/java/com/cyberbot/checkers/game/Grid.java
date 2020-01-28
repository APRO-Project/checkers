package com.cyberbot.checkers.game;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cyberbot.checkers.preferences.Preferences;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a grid in the checkers game. Basically, it is used to store {@link GridEntry} objects,
 * that represent player pieces and also to perform several game logic tasks such as possible
 * moves and captures calculations.
 *
 * It also implements {@link Iterable} so one can iterate conveniently over its entries.
 */
public class Grid implements Iterable<GridEntry>, Serializable {
    private final int size;
    private final int playerRows;
    private final ArrayList<GridEntry> gridEntries;
    private transient HashMap<GridEntry, ArrayList<Destination>> movableEntriesCache;

    private int moveCount = 0;

    // Preferences
    private boolean canMoveBackwards = false;
    private boolean canCaptureBackwards = true;
    private boolean flyingKing = true;
    private boolean mandatoryCapture = true;

    public int getSize() {
        return size;
    }

    public int getPlayerRows() {
        return playerRows;
    }

    /**
     * Default constructor, that creates {@link Grid} object
     * with 10x10 board and 4 player rows.
     */
    public Grid() {
        this(10, 4);
    }

    /**
     * Constructs {@link Grid} object with default {@link Preferences}.
     *
     * @param size Size of the board. Board's dimensions will be [{@code size} x {@code size}].
     *             Minimum value is 3
     * @param playerRows Number of player rows - must not be greater than {@code size / 2 - 1}
     */
    public Grid(int size, int playerRows) {
        if(size < 3) {
            throw new IllegalArgumentException("Size of the board should be at least 8");
        }
        if(size - (playerRows * 2) < 2) {
            throw new IllegalArgumentException("There should be 2 no-player rows on the board at minimum");
        }

        this.size = size;
        this.playerRows = playerRows;
        gridEntries = new ArrayList<>();
        movableEntriesCache = null;

        for (int i = 0; i < size * size; ++i) {
            int y = i / size;
            GridEntry entry = new GridEntry(i % size, y);

            if (y < playerRows && entry.legal()) {
                entry.setPlayer(PlayerNum.FIRST);
                entry.setPieceType(PieceType.ORDINARY);
            } else if (y >= size - playerRows && entry.legal()) {
                entry.setPlayer(PlayerNum.SECOND);
                entry.setPieceType(PieceType.ORDINARY);
            }

            gridEntries.add(entry);
        }
    }

    /**
     * Constructs {@link Grid} object.
     *
     * @param size See {@link Grid#Grid(int, int)}
     * @param playerRows See {@link Grid#Grid(int, int)}
     * @param canMoveBackwards Allow ordinary pieces to move backwards
     * @param canCaptureBackwards Allow ordinary pieces to capture backwards
     * @param flyingKing Allow king to move by multiple steps
     * @param mandatoryCapture Force captures
     */
    public Grid(int size, int playerRows, boolean canMoveBackwards, boolean canCaptureBackwards, boolean flyingKing, boolean mandatoryCapture) {
        this(size, playerRows);
        this.canMoveBackwards = canMoveBackwards;
        this.canCaptureBackwards = canCaptureBackwards;
        this.flyingKing = flyingKing;
        this.mandatoryCapture = mandatoryCapture;
    }

    /**
     * Constructs {@link Grid} object based on given {@link Preferences}.
     *
     * @param prefs {@link Preferences} object. Cannot be null
     * @return {@link Grid} object
     *
     * @see Preferences
     */
    @NotNull
    @Contract("_ -> new")
    public static Grid fromPreferences(@NotNull Preferences prefs) {
        return new Grid(
                prefs.getGridSize(),
                prefs.getPlayerRows(),
                // canMoveBackwards is always false when flyingKing is false
                prefs.getFlyingKing() && prefs.getCanMoveBackwards(),
                prefs.getCanCaptureBackwards(),
                prefs.getFlyingKing(),
                prefs.getMandatoryCapture()
        );
    }

    /**
     * Tells if given coordinates are valid indexes for {@link Grid#gridEntries}.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return {@code true} if coordinates are valid, {@code false} otherwise
     */
    @Contract(pure = true)
    private boolean coordsValid(int x, int y) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    /**
     * Look for specific {@link GridEntry} in {@link Grid#gridEntries}.
     *
     * @param x x-coordinate of searched {@link GridEntry}
     * @param y y-coordinate of searched {@link GridEntry}
     * @return {@link GridEntry} object if such exists in {@link Grid#gridEntries}. Otherwise, a
     * {@link RuntimeException} is thrown
     * @throws IndexOutOfBoundsException When called with {@code x} or {@code y} greater than
     * {@link Grid#size} or less than zero
     */
    @NotNull
    public GridEntry getEntryByCoords(int x, int y) throws IndexOutOfBoundsException {
        if (!coordsValid(x, y)) {
            throw new IndexOutOfBoundsException("Coordinates (" + x + ", " + y + ") out of bounds for grid with size " + size);
        }

        for (GridEntry e : gridEntries) {
            if (e.getX() == x && e.getY() == y) return e;
        }

        throw new RuntimeException("Entry (" + x + ", " + y + ") not found in Grid");
    }

    /**
     * Sets {@code player} as {@link PlayerNum#NOPLAYER} and {@code pieceType} as {@link PieceType#UNASSIGNED}
     * in {@link GridEntry} that matches given coordinates. When there is no such entry,
     * a {@link RuntimeException} is thrown.
     *
     * @param entry {@link GridEntry} to be "removed"
     *
     * @see PlayerNum
     * @see PieceType
     */
    public void removeGridEntry(@NotNull GridEntry entry) {
        int index = gridEntries.indexOf(entry);
        if(index == -1) {
            throw new RuntimeException("Entry (" + entry.getX() + ", " + entry.getY() + ") not found in gridEntries");
        }

        gridEntries.get(index).setPlayer(PlayerNum.NOPLAYER);
        gridEntries.get(index).setPieceType(PieceType.UNASSIGNED);
    }

    /**
     * Get {@link Destination} object based on given {@code src} and {@code dst} entries.
     *
     * @param src Source entry. Cannot be null
     * @param dst Destination entry. Cannot be null
     * @return {@link Destination} that matches given {@code dst}, new {@link Destination} with
     * {@code src} as the destination when {@code src == dst} or null when there is no destinations
     * for given {@code src} or {@code dst} is not in found in possible destinations
     *
     * @see Destination
     * @see Grid#getMovableEntries(PlayerNum)
     */
    @Nullable
    public Destination getDestination(@NotNull GridEntry src, @NotNull GridEntry dst) {
        if(src != dst) {
            ArrayList<Destination> destinations = getMovableEntries(src.getPlayer()).get(src);
            if (destinations == null) return null;

            for (Destination destination : destinations) {
                if (destination.getDestinationEntry() == dst) return destination;
            }
        } else return new Destination(src);

        return null;
    }

    /**
     * Tells if a move is allowed from {@code src} to {@code dst}.
     *
     * @param src Source entry. Cannot be null
     * @param dst Destination entry. Cannot be null
     * @return {@code true} if move from {@code src} to {@code dst} is allowed, {@code false} otherwise
     */
    public boolean destinationAllowed(@NotNull GridEntry src, @NotNull GridEntry dst) {
        if(src == dst) return true;

        getMovableEntries(src.getPlayer());

        ArrayList<Destination> destinations = movableEntriesCache.get(src);
        if (destinations == null) return false;

        for (Destination destination : destinations) {
            if (destination.getDestinationEntry() == dst) return true;
        }

        return false;
    }

    /**
     * Perform a move based on given {@code src} and {@code dst}. If the move is not
     * allowed based by {@link Grid#destinationAllowed(GridEntry, GridEntry)} decision or when
     * {@code src == dst}, it isn't executed.
     *
     * Moving a piece includes swapping {@code player} and {@code pieceType} of involved entries.
     * Also, when a piece get promotion if it's available.
     *
     * If any of entries is not found in {@link Grid#gridEntries}, a {@link RuntimeException} is
     * thrown.
     *
     * It also checks whether the game came to end or not.
     *
     * @param src Source entry. Cannot be null
     * @param dst Destination entry. Cannot be null
     * @param deleteCapturedPieces Specify if captured pieces should be deleted from the board or not
     * @param checkIfMoveAllowed Perform additional check if move from {@code src} to {@code dst} is allowed
     * @return {@code true} is move was executed or {@code false} if it's not allowed or {@link Grid#movableEntriesCache}
     * is null when invoked with {@code checkIfMoveAllowed} set to {@code false}
     *
     * @see Grid#promotionAvailable(GridEntry, GridEntry)
     * @see Grid#isGameOver()
     */
    private boolean attemptMove(@NotNull GridEntry src, @NotNull GridEntry dst, boolean deleteCapturedPieces, boolean checkIfMoveAllowed) {
        if(checkIfMoveAllowed && (src == dst || !destinationAllowed(src, dst))) return false;

        if(movableEntriesCache == null) return false;

        Destination destination = getDestination(src, dst);
        if(destination != null) {
            if(deleteCapturedPieces) {
                ArrayList<GridEntry> capturedPieces = destination.getCapturedPieces();
                if(capturedPieces != null) {
                    for(GridEntry captured: capturedPieces) {
                        final int idx = gridEntries.indexOf(captured);
                        gridEntries.get(idx).setPlayer(PlayerNum.NOPLAYER);
                        gridEntries.get(idx).setPieceType(PieceType.UNASSIGNED);
                    }
                }
            }

            // Count king moves (used in deciding whether to end the game with a draw)
            if(src.getPieceType() == PieceType.KING && !destination.isCapture()) ++moveCount;
            else moveCount = 0;  // Clear counter when an ordinary piece moved or there was a capture
        }

        final int srcIdx = gridEntries.indexOf(src);
        final int dstIdx = gridEntries.indexOf(dst);

        if (srcIdx == -1 || dstIdx == -1) {
            throw new RuntimeException("GridEntry destination or source not part of the Grid");
        }

        gridEntries.get(dstIdx).setPlayer(src.getPlayer());
        gridEntries.get(dstIdx).setPieceType(
                promotionAvailable(src, dst) ? PieceType.KING : src.getPieceType()
        );

        gridEntries.get(srcIdx).setPlayer(PlayerNum.NOPLAYER);
        gridEntries.get(srcIdx).setPieceType(PieceType.UNASSIGNED);

        movableEntriesCache = null;

        GameEnd gameEnd = isGameOver();
        if(gameEnd != null) {
            System.out.println("Game over: " + gameEnd.getReason());
        }

        return true;
    }

    /**
     * Overload method {@link Grid#attemptMove(GridEntry, GridEntry, boolean, boolean)} that doesn't
     * delete captured pieces and performs additional checks if the move is allowed or not.
     *
     * @param src Source entry. Cannot be null
     * @param dst Destination entry. Cannot be null
     * @return {@code true} is move was executed or {@code false} if it's not allowed
     */
    public boolean attemptMove(@NotNull GridEntry src, @NotNull GridEntry dst) {
        return attemptMove(src, dst, false, true);
    }

    @NotNull
    @Override
    public Iterator<GridEntry> iterator() {
        return gridEntries.iterator();
    }

    /**
     * Get all adjacent entries to {@code entry}. Only legal entries are considered adjacent.
     *
     * @param entry {@link GridEntry}, for which we want to get adjacent entries
     * @return {@link ArrayList} with all adjacent entries to given {@code entry}
     */
    @NotNull
    private ArrayList<GridEntry> getAdjacentEntries(@NotNull GridEntry entry) {
        ArrayList<GridEntry> adjacentEntries = new ArrayList<>();

        // Short names for code readability
        final int x = entry.getX();
        final int y = entry.getY();

        // Bottom corner
        if (x == 0 && y == size - 1) {
            adjacentEntries.add(getEntryByCoords(1, size - 2));
        }
        // Upper corner
        else if (y == 0 && x == size - 1) {
            adjacentEntries.add(getEntryByCoords(size - 2, 1));
        }
        // Upper edge
        else if (x == 0) {
            adjacentEntries.add(getEntryByCoords(1, y - 1));
            adjacentEntries.add(getEntryByCoords(1, y + 1));
        }
        // Left edge
        else if (y == 0) {
            adjacentEntries.add(getEntryByCoords(x - 1, 1));
            adjacentEntries.add(getEntryByCoords(x + 1, 1));
        }
        // Bottom edge
        else if (x == size - 1) {
            adjacentEntries.add(getEntryByCoords(x - 1, y - 1));
            adjacentEntries.add(getEntryByCoords(x - 1, y + 1));
        }
        // Right edge
        else if (y == size - 1) {
            adjacentEntries.add(getEntryByCoords(x - 1, y - 1));
            adjacentEntries.add(getEntryByCoords(x + 1, y - 1));
        }
        // Mid entries
        else {
            adjacentEntries.add(getEntryByCoords(x - 1, y - 1));
            adjacentEntries.add(getEntryByCoords(x - 1, y + 1));
            adjacentEntries.add(getEntryByCoords(x + 1, y - 1));
            adjacentEntries.add(getEntryByCoords(x + 1, y + 1));
        }

        return adjacentEntries;
    }

    /**
     * Get all allowed moves for specified {@code entry}. Moves are calculated differently
     * based on {@link PieceType}.
     *
     * If {@code entry} is {@link PieceType#UNASSIGNED}, then a {@link IllegalArgumentException}
     * is thrown.
     *
     * @param entry {@link GridEntry} we want to get moves for
     * @return {@link ArrayList} of allowed moves
     *
     * @see Grid#calculateOrdinaryPieceMoves(GridEntry)
     * @see Grid#calculateKingMoves(GridEntry)
     */
    @NotNull
    private ArrayList<GridEntry> getAllowedMoves(@NotNull GridEntry entry) {
        if(entry.getPieceType() == PieceType.UNASSIGNED) {
            throw new IllegalArgumentException("Attempt to get allowed moves for UNASSIGNED piece type");
        }

        ArrayList<GridEntry> allowedMoves = new ArrayList<>();

        if (entry.getPieceType() == PieceType.ORDINARY) {
            allowedMoves = calculateOrdinaryPieceMoves(entry);
        }
        else if(entry.getPieceType() == PieceType.KING) {
            allowedMoves = calculateKingMoves(entry);
        }

        return allowedMoves;
    }

    /**
     * Calculate allowed moves for {@link PieceType#ORDINARY}. {@link Grid#canMoveBackwards}
     * preference is taken into account during calculations.
     *
     * If supplied {@code entry} has different {@code pieceType} than {@link PieceType#ORDINARY},
     * a {@link IllegalArgumentException} is thrown.
     *
     * @param entry {@link GridEntry} we want to calculate moves for
     * @return {@link ArrayList} of calculated moves
     */
    @NotNull
    private ArrayList<GridEntry> calculateOrdinaryPieceMoves(@NotNull GridEntry entry) {
        if(entry.getPieceType() != PieceType.ORDINARY) {
            throw new IllegalArgumentException("Attempt to calculate ordinary piece moves for non-ordinary piece");
        }

        ArrayList<GridEntry> allowedMoves = new ArrayList<>();
        for (GridEntry adjEntry : getAdjacentEntries(entry)) {
            if (adjEntry.getPlayer() == PlayerNum.NOPLAYER) {
                if (canMoveBackwards
                        || (entry.getPlayer() == PlayerNum.FIRST && entry.getY() < adjEntry.getY())
                        || (entry.getPlayer() == PlayerNum.SECOND && entry.getY() > adjEntry.getY())) {
                    allowedMoves.add(adjEntry);
                }
            }
        }

        return allowedMoves;
    }

    /**
     * Calculate allowed moves for {@link PieceType#KING}. {@link Grid#flyingKing} preference
     * is taken into account during calculations.
     *
     * If supplied {@code entry} has different {@code pieceType} than {@link PieceType#KING},
     * a {@link IllegalArgumentException} is thrown.
     *
     * @param entry {@link GridEntry} we want to calculate moves for
     * @return {@link ArrayList} of calculated moves
     */
    @NotNull
    private ArrayList<GridEntry> calculateKingMoves(@NotNull GridEntry entry) {
        if(entry.getPieceType() != PieceType.KING) {
            throw new IllegalArgumentException("Attempt to calculate king moves for non-king");
        }

        ArrayList<GridEntry> allowedMoves = new ArrayList<>();
        for(GridEntry adjEntry: getAdjacentEntries(entry)) {
            if(adjEntry.getPlayer() == PlayerNum.NOPLAYER) {
                allowedMoves.add(adjEntry);

                // Move diagonally in direction implied by ajdEntry to discover more free entries
                if(flyingKing) {
                    final int directionX = adjEntry.getX() - entry.getX();
                    final int directionY = adjEntry.getY() - entry.getY();

                    int nextX = adjEntry.getX() + directionX;
                    int nextY = adjEntry.getY() + directionY;
                    while(coordsValid(nextX, nextY)) {
                        GridEntry nextEntry = getEntryByCoords(nextX, nextY);
                        if(nextEntry.getPlayer() == PlayerNum.NOPLAYER) {
                            allowedMoves.add(nextEntry);
                            nextX += directionX;
                            nextY += directionY;
                        } else break;
                    }
                }
            }
        }

        return allowedMoves;
    }

    /**
     * Get all allowed captures for specified {@code entry}. Captures are calculated differently
     * based on {@link PieceType}
     *
     * @param entry {@link GridEntry} we want to get captures for
     * @return {@link ArrayList} of allowed captures
     *
     * @see Grid#calculateOrdinaryPieceCaptures(CaptureChain, PlayerNum)
     * @see Grid#calculateKingCaptures(CaptureChain, PlayerNum)
     */
    @NotNull
    private ArrayList<CaptureChain> getAllowedCaptures(@NotNull GridEntry entry) {
        CaptureChain root = new CaptureChain(entry);

        if (entry.getPieceType() == PieceType.ORDINARY) {
            calculateOrdinaryPieceCaptures(root, entry.getPlayer());
        }
        else if(entry.getPieceType() == PieceType.KING) {
            calculateKingCaptures(root, entry.getPlayer());
        }

        ArrayList<CaptureChain> allowedCaptures;

        if (mandatoryCapture) allowedCaptures = root.getLongestCaptures();
        else allowedCaptures = root.getAllCaptures();

        return allowedCaptures;
    }

    /**
     * Calculate allowed captures for {@link PieceType#ORDINARY}. {@link Grid#canCaptureBackwards}
     * preference is taken into account during calculations.
     *
     * Method doesn't return any value - calculations are performed recursively and results are
     * stored in {@code lastCapture} parameter.
     *
     * There's no checks if the method was invoked for {@link PieceType#ORDINARY} because of
     * recursive nature of it, so the caller should be sure to call it correctly. Also, the caller
     * needs to be sure, that {@code player} is not set to {@link PlayerNum#NOPLAYER}.
     *
     * @param lastCapture {@link CaptureChain} object representing last capture that has taken place.
     *                                        It should have been set to root capture when first invoked
     * @param player {@link PlayerNum} representing the player who is capturing
     *
     * @see CaptureChain
     */
    private void calculateOrdinaryPieceCaptures(@NotNull CaptureChain lastCapture, final PlayerNum player) {
        GridEntry lastLocation = lastCapture.getLocationAfterCapture();

        for (GridEntry adjEntry : getAdjacentEntries(lastLocation)) {
            // Check if can capture backwards or adjacent piece is ahead
            if (canCaptureBackwards
                    || (player == PlayerNum.FIRST && lastLocation.getY() < adjEntry.getY())
                    || (player == PlayerNum.SECOND && lastLocation.getY() > adjEntry.getY())) {

                // Check if adjacent piece belongs to other player and wasn't captured yet
                if (adjEntry.getPlayer() != PlayerNum.NOPLAYER
                        && adjEntry.getPlayer() != player
                        && !lastCapture.checkIfEntryCaptured(adjEntry)) {

                    final int entryAfterX = adjEntry.getX() + (adjEntry.getX() - lastLocation.getX());
                    final int entryAfterY = adjEntry.getY() + (adjEntry.getY() - lastLocation.getY());

                    // Check if entry after captured piece is on the Grid
                    if (coordsValid(entryAfterX, entryAfterY)) {
                        GridEntry entryAfter = getEntryByCoords(entryAfterX, entryAfterY);
                        // Check if entry after captured piece belongs to nobody
                        if (entryAfter.getPlayer() == PlayerNum.NOPLAYER) {
                            CaptureChain nextCapture = new CaptureChain(entryAfter, adjEntry, lastCapture);
                            lastCapture.addNextCapture(nextCapture);
                            calculateOrdinaryPieceCaptures(nextCapture, player);
                        }
                    }
                }
            }
        }
    }

    /**
     * Calculate allowed captures for {@link PieceType#KING}. {@link Grid#flyingKing} preference
     * is taken into account during calculations.
     *
     * Method doesn't return any value - calculations are preformed recursively and results are
     * stored in {@code lastCapture} parameters.
     *
     * There's no checks if the method was invoked for {@link PieceType#KING} because of recursive
     * nature of it, so the caller should be sure to call it correctly. Also, the caller needs
     * to be sure, that {@code player} is not set to {@link PlayerNum#NOPLAYER}.
     *
     * @param lastCapture {@link CaptureChain} object representing last capture that has taken place.
     *                                        It should have been set to root capture when first invoked
     * @param player {@link PlayerNum} representing the player who is capturing
     *
     * @see CaptureChain
     */
    private void calculateKingCaptures(@NotNull CaptureChain lastCapture, final PlayerNum player) {
        GridEntry lastLocation = lastCapture.getLocationAfterCapture();

        for(GridEntry adjEntry: getAdjacentEntries(lastLocation)) {
            final int directionX = adjEntry.getX() - lastLocation.getX();
            final int directionY = adjEntry.getY() - lastLocation.getY();

            GridEntry enemyEntry = null;

            // Move diagonally to find enemy piece in range
            if(adjEntry.getPlayer() == PlayerNum.NOPLAYER && flyingKing) {
                int nextX = adjEntry.getX();
                int nextY = adjEntry.getY();

                while(coordsValid((nextX += directionX), (nextY += directionY))) {
                    GridEntry entryAfter = getEntryByCoords(nextX, nextY);

                    if(entryAfter.getPlayer() != PlayerNum.NOPLAYER) {
                        if(entryAfter.getPlayer() != player) enemyEntry = entryAfter;
                        break;
                    }
                }
            }
            // Enemy piece is adjacent
            else if(adjEntry.getPlayer() != PlayerNum.NOPLAYER && adjEntry.getPlayer() != player) {
                enemyEntry = adjEntry;
            }

            // Skip if enemy piece already captured or there's no one in range
            if(enemyEntry == null || lastCapture.checkIfEntryCaptured(enemyEntry)) continue;

            // Check for free spaces after enemy piece
            int nextX = enemyEntry.getX();
            int nextY = enemyEntry.getY();

            if(flyingKing) {
                while(coordsValid((nextX += directionX), (nextY += directionY))) {
                    GridEntry entryAfter = getEntryByCoords(nextX, nextY);
                    if(entryAfter.getPlayer() == PlayerNum.NOPLAYER) {
                        CaptureChain nextCapture = new CaptureChain(entryAfter, enemyEntry, lastCapture);
                        lastCapture.addNextCapture(nextCapture);
                        calculateKingCaptures(nextCapture, player);
                    } else break;
                }
            }
            else {  // Necessary duplicate code to avoid checks in previous while loop
                if(coordsValid((nextX += directionX), (nextY += directionY))) {
                    GridEntry entryAfter = getEntryByCoords(nextX, nextY);
                    if(entryAfter.getPlayer() == PlayerNum.NOPLAYER) {
                        CaptureChain nextCapture = new CaptureChain(entryAfter, enemyEntry, lastCapture);
                        lastCapture.addNextCapture(nextCapture);
                        calculateKingCaptures(nextCapture, player);
                    }
                }
            }
        }
    }

    /**
     * Tell if piece identified with {@code src} can be promoted to {@link PieceType#KING}.
     *
     * When given {@code src} belongs to {@link PlayerNum#NOPLAYER}, a {@link RuntimeException}
     * is thrown.
     *
     * @param src Piece to be promoted
     * @param dst Destination to where the piece is heading
     * @return {@code true} if piece can be promoted, {@code false} otherwisea
     */
    public boolean promotionAvailable(@NotNull GridEntry src, @NotNull GridEntry dst) {
        if(src.getPlayer() == PlayerNum.NOPLAYER){
            throw new RuntimeException("Attempt to check for promotion for NOPLAYER entry");
        }

        if(src.getPieceType() == PieceType.KING) return false;

        return src.getPlayer() == PlayerNum.FIRST ? dst.getY() == size-1 : dst.getY() == 0;
    }

    /**
     * Check if the game has come to end.
     *
     * @return {@link GameEnd} object with information about game ending or null if the game hasn't
     * ended yet
     *
     * @see GameEnd
     */
    @Nullable
    public GameEnd isGameOver() {
        if(moveCount == 25) {
            return new GameEnd(PlayerNum.NOPLAYER, GameEndReason.DRAW_TOO_MANY_KING_ONLY_MOVES);
        }

        int firstPlayerPieces = 0;
        int firstPlayerKings = 0;

        int secondPlayerPieces = 0;
        int secondPlayerKings = 0;

        for(GridEntry entry: gridEntries) {
            if(entry.getPlayer() == PlayerNum.FIRST) {
                if(entry.getPieceType() == PieceType.KING) ++firstPlayerKings;
                else ++firstPlayerPieces;
            }
            else if(entry.getPlayer() == PlayerNum.SECOND) {
                if(entry.getPieceType() == PieceType.KING) ++secondPlayerKings;
                else ++secondPlayerPieces;
            }
        }

        if(secondPlayerKings + secondPlayerPieces == 0) {
            return new GameEnd(PlayerNum.FIRST, GameEndReason.WIN_OPPONENT_NO_PIECES_REMAINING);
        }

        if(firstPlayerKings + firstPlayerPieces == 0) {
            return new GameEnd(PlayerNum.SECOND, GameEndReason.WIN_OPPONENT_NO_PIECES_REMAINING);
        }

        if(firstPlayerKings == 1 && secondPlayerKings == 1 && firstPlayerPieces == 0 && secondPlayerPieces == 0) {
            return new GameEnd(PlayerNum.NOPLAYER, GameEndReason.DRAW_KING_VS_KING);
        }

        final int firstPlayerMovablePieces = getMovableEntries(PlayerNum.FIRST, false).size();
        final int secondPlayerMovablePieces = getMovableEntries(PlayerNum.SECOND, false).size();

        if(firstPlayerMovablePieces == 0 && secondPlayerMovablePieces == 0) {
            return new GameEnd(PlayerNum.NOPLAYER, GameEndReason.DRAW_NO_MOVABLE_PIECES_REMAINING);
        }

        if(firstPlayerMovablePieces == 0) {
            return new GameEnd(PlayerNum.FIRST, GameEndReason.WIN_OPPONENT_NO_MOVABLE_PIECES_REMAINING);
        }

        if(secondPlayerMovablePieces == 0) {
            return new GameEnd(PlayerNum.SECOND, GameEndReason.WIN_OPPONENT_NO_MOVABLE_PIECES_REMAINING);
        }

        movableEntriesCache = null;

        return null;
    }

    /**
     * Get all movable entries for given {@code player}. This includes all moves and captures,
     * respecting the {@link Grid#mandatoryCapture} preference.
     *
     * Results are saved in {@link Grid#movableEntriesCache} because of high calling rate
     * and quite complex operations withing the method. The cache should be cleared after
     * {@link Grid#attemptMove(GridEntry, GridEntry)} is successfully called ({@code true} returned).
     *
     * {@code player} should not be {@link PlayerNum#NOPLAYER} - otherwise {@link IllegalArgumentException}
     * is thrown.
     *
     * @param player Player we want to get movable entries for
     * @param useCache Whether to use {@link Grid#movableEntriesCache} to restore/save results
     * @return {@link HashMap}, where {@link GridEntry} is the key and represents source of the move
     * and {@link ArrayList} of {@link Destination} that represents all possible destinations for the
     * source entry
     */
    @NotNull
    private HashMap<GridEntry, ArrayList<Destination>> getMovableEntries(PlayerNum player, boolean useCache) {
        if(player == PlayerNum.NOPLAYER) {
            throw new IllegalArgumentException("Cannot get movable entries for NOPLAYER");
        }

        if(useCache && movableEntriesCache != null) return movableEntriesCache;

        HashMap<GridEntry, ArrayList<Destination>> movableEntries = new HashMap<>();
        HashMap<GridEntry, ArrayList<CaptureChain>> possibleCaptures = new HashMap<>();
        int longestCaptureLength = 1;

        // Check for captures first
        for (GridEntry entry : gridEntries) {
            if (entry.getPlayer() == player) {
                ArrayList<CaptureChain> allowedCaptures = getAllowedCaptures(entry);
                if (!allowedCaptures.isEmpty()) {
                    possibleCaptures.put(entry, getAllowedCaptures(entry));
                    for (CaptureChain capture : allowedCaptures) {
                        if (capture.getCaptureLength() > longestCaptureLength) {
                            longestCaptureLength = capture.getCaptureLength();
                        }
                    }
                }
            }
        }

        // Add moves if captures aren't mandatory
        if (!mandatoryCapture || possibleCaptures.isEmpty()) {
            for (GridEntry entry : gridEntries) {
                if (entry.getPlayer() == player) {
                    ArrayList<GridEntry> allowedMoves = getAllowedMoves(entry);
                    if (!allowedMoves.isEmpty()) {
                        ArrayList<Destination> destinations = new ArrayList<>();
                        for (GridEntry destination : allowedMoves) {
                            destinations.add(new Destination(destination));
                        }

                        movableEntries.put(entry, destinations);
                    }
                }
            }
        }

        // Add captures
        if (!possibleCaptures.isEmpty()) {
            for (Map.Entry<GridEntry, ArrayList<CaptureChain>> possibleCapture : possibleCaptures.entrySet()) {
                ArrayList<Destination> destinations = new ArrayList<>();
                for (CaptureChain capture : possibleCapture.getValue()) {
                    if (!mandatoryCapture || capture.getCaptureLength() == longestCaptureLength) {
                        destinations.add(new Destination(capture));
                    }
                }

                if (!destinations.isEmpty()) {
                    if (!movableEntries.containsKey(possibleCapture.getKey())) {
                        movableEntries.put(possibleCapture.getKey(), destinations);
                    } else {
                        ArrayList<Destination> otherDestinations = movableEntries.get(possibleCapture.getKey());
                        if(otherDestinations == null) {
                            throw new RuntimeException("This should not happen. Contact developers on GitHub if it did");
                        }
                        otherDestinations.addAll(destinations);
                    }
                }
            }
        }

        if(useCache) movableEntriesCache = movableEntries;

        return movableEntries;
    }

    /**
     * Overload of {@link Grid#getMovableEntries(PlayerNum, boolean)} that uses cache by default.
     *
     * @param player Player we want to get movable entries for
     * @return {@link HashMap}, where {@link GridEntry} is the key and represents source of the move
     * and {@link ArrayList} of {@link Destination} that represents all possible destinations for the
     * source entry
     */
    @NotNull
    public HashMap<GridEntry, ArrayList<Destination>> getMovableEntries(PlayerNum player) {
        return getMovableEntries(player, true);
    }

    int getValue(PlayerNum playerNum, PlayerNum adversaryNum) {

        int value = 0;
        for (GridEntry gridEntry : gridEntries) {
            if (gridEntry.getPlayer() == playerNum) {

                value += 20;

                if (gridEntry.getPieceType() == PieceType.KING) {
                    value += 80;
                }

                // Prioritize sides
                value += Math.abs(gridEntry.getX() + 1 - (size / 2));

                //Prioritize forward movement

                if (playerNum == PlayerNum.FIRST) {
                    value += gridEntry.getY() + 1;
                } else if (playerNum == PlayerNum.SECOND) {
                    value += size - (gridEntry.getY() + 1);
                }

            } else if (gridEntry.getPlayer() == adversaryNum) {

                value -= 20;

                if (gridEntry.getPieceType() == PieceType.KING) {
                    value -= 80;
                }

                // Prioritize sides
                value -= Math.abs(gridEntry.getX() + 1 - (size / 2));

                //Prioritize forward movement

                if (adversaryNum == PlayerNum.FIRST) {
                    value -= gridEntry.getY() + 1;
                } else if (adversaryNum == PlayerNum.SECOND) {
                    value -= size - (gridEntry.getY() + 1);
                }
            }
        }

        return value;
    }

    static Grid simulateMove(Grid startGrid, GridEntry src, Destination destination) {
        Grid grid = SerializationUtils.clone(startGrid);

        GridEntry srcEntry = grid.getEntryByCoords(src.getX(), src.getY());

        srcEntry.setPlayer(PlayerNum.NOPLAYER);
        srcEntry.setPieceType(PieceType.UNASSIGNED);

        ArrayList<GridEntry> capturedPieces = destination.getCapturedPieces();
        if (capturedPieces != null) {
            for (GridEntry destroyed : destination.getCapturedPieces()) {
                GridEntry destroyedEntry = grid.getEntryByCoords(destroyed.getX(), destroyed.getY());

                destroyedEntry.setPieceType(PieceType.UNASSIGNED);
                destroyedEntry.setPlayer(PlayerNum.NOPLAYER);
            }
        }

        int dstX = destination.getDestinationEntry().getX();
        int dstY = destination.getDestinationEntry().getY();

        GridEntry dst = grid.getEntryByCoords(dstX, dstY);
        dst.setPlayer(src.getPlayer());
        dst.setPieceType(src.getPieceType());

        return grid;
    }

    boolean won(PlayerNum enemy) {
        if (getMovableEntries(enemy).isEmpty()) {
            return true;
        }
        int enemyNo = 0;
        for (GridEntry gridEntry : gridEntries) {
            if (gridEntry.getPlayer() == enemy) {
                enemyNo++;
            }
        }
        return enemyNo == 0;
    }

    boolean lost(PlayerNum player) {
        if (getMovableEntries(player).isEmpty()) {
            return true;
        }
        int playerNo = 0;
        for (GridEntry gridEntry : gridEntries) {
            if (gridEntry.getPlayer() == player) {
                playerNo++;
            }
        }
        return playerNo == 0;
    }
}