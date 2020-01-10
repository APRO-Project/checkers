package com.cyberbot.checkers.game.pieces;

import com.cyberbot.checkers.game.Grid;
import com.cyberbot.checkers.game.GridEntry;
import com.cyberbot.checkers.game.PlayerNum;
import com.cyberbot.checkers.preferences.Preferences;

import java.util.HashSet;
import java.util.Queue;

public abstract class Piece {
    protected final PlayerNum player;
    protected GridEntry location;

    static Grid grid = null;
    static Preferences prefs = null;

    protected HashSet<GridEntry> allowedMoves;
    protected HashSet<Queue<GridEntry>> allowedCaptures;

    public Piece(GridEntry gridEntry, PlayerNum playerNum) {
        if(playerNum == PlayerNum.NOPLAYER) {
            throw new IllegalArgumentException("playerNum should be FIRST or SECOND for Piece");
        }

        player = playerNum;
        changeLocation(gridEntry);
        allowedMoves = new HashSet<>();
        allowedCaptures = new HashSet<>();
    }

    public static void setPieceEnvironment(Preferences prefs, Grid grid) {
        prefs = prefs;
        grid = grid;
    }

    public PlayerNum getPlayer() { return player; }

    public void changeLocation(GridEntry newLocation) {
        if(!newLocation.legal()) {
            throw new RuntimeException("Attempt to move a Piece to invalid location");
        }

        if(newLocation.getPlayer() != player && newLocation.getPlayer() != PlayerNum.NOPLAYER) {
            throw new RuntimeException("New location does not belong to player " + player);
        }

        location = newLocation;
        allowedMoves.clear();
        allowedCaptures.clear();
    }

    public GridEntry getLocation() { return location; }

    public HashSet<GridEntry> getAllowedMoves() { return allowedMoves; }

    public HashSet<Queue<GridEntry>> getAllowedCaptures() { return allowedCaptures; }

    protected final HashSet<GridEntry> getAdjacentEntries() {
        if(prefs == null || grid == null) {
            throw new RuntimeException("Uninitialized static variables: prefs and/or grid");
        }

        HashSet<GridEntry> adjacentEntries = new HashSet<>();

        // Short names for code readability
        final int gridSize = prefs.getGridSize();
        final int x = location.getX();
        final int y = location.getY();

        // Bottom corner
        if(x == 0 && y == gridSize-1) {
              adjacentEntries.add(grid.getEntryByCoords(1, gridSize-2));
        }
        // Upper corner
        else if(y == 0 && x == gridSize-1) {
            adjacentEntries.add(grid.getEntryByCoords(gridSize-2, 1));
        }
        // Upper edge
        else if(x == 0) {
            adjacentEntries.add(grid.getEntryByCoords(1, y-1));
            adjacentEntries.add(grid.getEntryByCoords(1, y+1));
        }
        // Left edge
        else if(y == 0) {
            adjacentEntries.add(grid.getEntryByCoords(x-1, 1));
            adjacentEntries.add(grid.getEntryByCoords(x+1, 1));
        }
        // Bottom edge
        else if(x == gridSize-1) {
            adjacentEntries.add(grid.getEntryByCoords(x-1, y-1));
            adjacentEntries.add(grid.getEntryByCoords(x-1, y+1));
        }
        // Right edge
        else if(y == gridSize-1) {
            adjacentEntries.add(grid.getEntryByCoords(x-1, y-1));
            adjacentEntries.add(grid.getEntryByCoords(x+1, y-1));
        }
        // Mid entries
        else {
            adjacentEntries.add(grid.getEntryByCoords(x-1, y-1));
            adjacentEntries.add(grid.getEntryByCoords(x-1, y+1));
            adjacentEntries.add(grid.getEntryByCoords(x+1, y-1));
            adjacentEntries.add(grid.getEntryByCoords(x+1, y+1));
        }

        return adjacentEntries;
    }
}

interface MoveCalculations {
    HashSet<GridEntry> calculateAllowedMoves();
    HashSet<Queue<GridEntry>> calculateAllowedCaptures();
}