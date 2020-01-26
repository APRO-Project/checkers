package com.cyberbot.checkers.game;

import androidx.annotation.NonNull;

import com.cyberbot.checkers.preferences.Preferences;

import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class Grid implements Iterable<GridEntry>, Serializable {
    private final int size;
    private final int playerRows;
    private final ArrayList<GridEntry> gridEntries;
    private transient HashMap<GridEntry, ArrayList<Destination>> movableEntriesCache;

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

    public Grid() {
        this(10, 4);
    }

    public Grid(int size, int playerRows) {
        if (size - (playerRows * 2) < 2) {
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

    public Grid(int size, int playerRows, boolean canMoveBackwards, boolean canCaptureBackwards, boolean flyingKing, boolean mandatoryCapture) {
        this(size, playerRows);
        this.canMoveBackwards = canMoveBackwards;
        this.canCaptureBackwards = canCaptureBackwards;
        this.flyingKing = flyingKing;
        this.mandatoryCapture = mandatoryCapture;
    }

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

    public GridEntry getEntryByCoords(int x, int y) throws IndexOutOfBoundsException {
        if (x >= size || y >= size) {
            throw new IndexOutOfBoundsException("Coordinates (" + x + ", " + y + ") out of bounds for grid with size " + size);
        }

        for (GridEntry e : gridEntries) {
            if (e.getX() == x && e.getY() == y) return e;
        }

        throw new RuntimeException("Entry (" + x + ", " + y + ") not found in Grid");
    }

    public void removeGridEntry(GridEntry entry) {
        int index = gridEntries.indexOf(entry);
        gridEntries.get(index).setPlayer(PlayerNum.NOPLAYER);
        gridEntries.get(index).setPieceType(PieceType.UNASSIGNED);
    }

    public Destination getDestination(GridEntry src, GridEntry dst) {
        if (src != dst) {
            ArrayList<Destination> destinations = getMovableEntries(src.getPlayer()).get(src);
            if (destinations == null) return null;

            for (Destination destination : destinations) {
                if (destination.getDestinationEntry() == dst) return destination;
            }
        } else return new Destination(src);

        return null;
    }

    public boolean destinationAllowed(GridEntry src, GridEntry dst) {
        if (src == dst) return true;

        getMovableEntries(src.getPlayer());

        ArrayList<Destination> destinations = movableEntriesCache.get(src);
        if (destinations == null) return false;

        for (Destination destination : destinations) {
            if (destination.getDestinationEntry() == dst) return true;
        }

        return false;
    }

    public boolean attemptMove(GridEntry src, GridEntry dst) {
        if (src == dst) return false;

        if (!destinationAllowed(src, dst)) return false;

        final int srcIdx = gridEntries.indexOf(src);
        final int dstIdx = gridEntries.indexOf(dst);

        if (srcIdx == -1 || dstIdx == -1) {
            throw new RuntimeException("GridEntry destination or source not part of the Grid");
        }

        gridEntries.get(dstIdx).setPlayer(src.getPlayer());
        gridEntries.get(dstIdx).setPieceType(src.getPieceType());

        gridEntries.get(srcIdx).setPlayer(PlayerNum.NOPLAYER);
        gridEntries.get(srcIdx).setPieceType(PieceType.UNASSIGNED);

        movableEntriesCache = null;

        return true;
    }

    @NonNull
    @Override
    public Iterator<GridEntry> iterator() {
        return gridEntries.iterator();
    }

    private HashSet<GridEntry> getAdjacentEntries(@NotNull GridEntry entry) {
        HashSet<GridEntry> adjacentEntries = new HashSet<>();

        // Short name for code readability
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

    private HashSet<GridEntry> getAllowedMoves(@NotNull GridEntry entry) {
        HashSet<GridEntry> allowedMoves = new HashSet<>();

        if (entry.getPieceType() == PieceType.ORDINARY) {
            allowedMoves = calculateOrdinaryPieceMoves(entry);
        } else if (entry.getPieceType() == PieceType.KING) {
            // TODO: Calculate king moves
        }

        return allowedMoves;
    }

    private HashSet<GridEntry> calculateOrdinaryPieceMoves(@NotNull GridEntry entry) {
        HashSet<GridEntry> allowedMoves = new HashSet<>();
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

    private ArrayList<CaptureChain> getAllowedCaptures(@NotNull GridEntry entry) {
        CaptureChain root = new CaptureChain(entry, null, null);

        if (entry.getPieceType() == PieceType.ORDINARY) {
            calculateOrdinaryPieceCaptures(root, entry.getPlayer());
        } else if (entry.getPieceType() == PieceType.KING) {
            // TODO: Calculate king captures
        }

        ArrayList<CaptureChain> allowedCaptures;

        if (mandatoryCapture) allowedCaptures = root.getLongestCaptures();
        else allowedCaptures = root.getAllCaptures();

        return allowedCaptures;
    }

    private void calculateOrdinaryPieceCaptures(@NotNull CaptureChain lastCapture, final PlayerNum player) {
        GridEntry lastLocation = lastCapture.getLocationAfterCapture();

        for (GridEntry adjEntry : getAdjacentEntries(lastLocation)) {
            // Check if can capture backwards or adjacent piece is ahead
            if (canCaptureBackwards
                    || (lastLocation.getPlayer() == PlayerNum.FIRST && lastLocation.getY() < adjEntry.getY())
                    || (lastLocation.getPlayer() == PlayerNum.SECOND && lastLocation.getY() > adjEntry.getY())) {

                // Check if adjacent piece belongs to other player and wasn't captured yet
                if (adjEntry.getPlayer() != PlayerNum.NOPLAYER
                        && adjEntry.getPlayer() != player
                        && !lastCapture.checkIfEntryCaptured(adjEntry)) {

                    int entryAfterX = adjEntry.getX() + (adjEntry.getX() - lastLocation.getX());
                    int entryAfterY = adjEntry.getY() + (adjEntry.getY() - lastLocation.getY());

                    // Check if entry after captured piece is on the Grid
                    if (entryAfterX >= 0 && entryAfterX < size
                            && entryAfterY >= 0 && entryAfterY < size) {

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

    public HashMap<GridEntry, ArrayList<Destination>> getMovableEntries(PlayerNum player) {
        if (movableEntriesCache != null) return movableEntriesCache;

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
                    HashSet<GridEntry> allowedMoves = getAllowedMoves(entry);
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
                        movableEntries.get(possibleCapture.getKey()).addAll(destinations);
                    }
                }
            }
        }
        movableEntriesCache = movableEntries;

        return movableEntries;
    }

    int getValue(PlayerNum playerNum, PlayerNum adversaryNum) {

        int value = 0;
        for (GridEntry gridEntry : gridEntries) {
            if (gridEntry.getPlayer() == playerNum) {

                //count pieces

                value += 10;

                if (gridEntry.getPieceType() == PieceType.KING) {
                    value += 30;
                }

                //prioritize sides

                value += Math.abs((gridEntry.getX()) + gridEntry.getY() - getSize()) / 4;

                //prioritize forward

                if (gridEntry.getPlayer() == PlayerNum.FIRST) {
                    value += gridEntry.getY() / 2;
                } else if (gridEntry.getPlayer() == PlayerNum.SECOND) {
                    value += Math.abs((gridEntry.getY() - getSize()) / 2);
                }
            } else if (gridEntry.getPlayer() == adversaryNum) {

                //count pieces

                value -= 10;

                if (gridEntry.getPieceType() == PieceType.KING) {
                    value -= 30;
                }

                //prioritize sides

                value -= Math.abs((gridEntry.getX()) + gridEntry.getY() - getSize()) / 4;

                //prioritize forward

                if (gridEntry.getPlayer() == PlayerNum.FIRST) {
                    value -= gridEntry.getY() / 2;
                } else if (gridEntry.getPlayer() == PlayerNum.SECOND) {
                    value -= Math.abs((gridEntry.getY() - getSize()) / 2);
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

        for (GridEntry destroyed : destination.getCapturedPieces()) {
            GridEntry destroyedEntry = grid.getEntryByCoords(destroyed.getX(), destroyed.getY());

            destroyedEntry.setPieceType(PieceType.UNASSIGNED);
            destroyedEntry.setPlayer(PlayerNum.NOPLAYER);
        }

        int dstX = destination.getDestinationEntry().getX();
        int dstY = destination.getDestinationEntry().getY();

        GridEntry dst = grid.getEntryByCoords(dstX, dstY);
        dst.setPlayer(PlayerNum.NOPLAYER);
        dst.setPieceType(PieceType.UNASSIGNED);
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