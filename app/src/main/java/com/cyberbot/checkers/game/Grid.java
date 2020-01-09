package com.cyberbot.checkers.game;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;

public class Grid implements Iterable<GridEntry> {
    private int size;
    private int playerRows;
    private ArrayList<GridEntry> gridEntries;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPlayerRows() {
        return playerRows;
    }

    public void setPlayerRows(int playerRows) {
        this.playerRows = playerRows;
    }

    public Grid(int size, int playerRows) {
        if(size - (playerRows * 2) < 2)
            throw new IllegalArgumentException("There should be 2 no-player rows on the board at minimum");

        this.size = size;
        this.playerRows = playerRows;
        gridEntries = new ArrayList<>();

        for(int i = 0; i < size*size; ++i) {
            int y = i / size;
            GridEntry entry = new GridEntry(i % size, y);

            if(y < playerRows && entry.legal())
                entry.setPlayer(PlayerNum.FIRST);
            else if(y >= size - playerRows && entry.legal())
                entry.setPlayer(PlayerNum.SECOND);

            gridEntries.add(entry);
        }
    }

    public Grid() {
        this(8, 3);
    }

    public GridEntry getEntryByCoords(int x, int y) throws IndexOutOfBoundsException {
        if(x >= size || y >= size)
            throw new IndexOutOfBoundsException("Coordinates (" + x + ", " + y + ") out of bounds for grid with size " + size);

        for(GridEntry e: gridEntries) {
            if(e.getX() == x && e.getY() == y)
                return e;
        }

        throw new RuntimeException("Entry (" + x + ", " + y + ") not found in Grid");
    }

    public boolean moveAllowed(GridEntry src, GridEntry dst) {
        return src == dst || (dst.getPlayer() == PlayerNum.NOPLAYER && dst.legal());
    }

    public boolean attemptMove(GridEntry src, GridEntry dst) {
        if(dst == src || !moveAllowed(src, dst))
            return false;

        int srcIdx = gridEntries.indexOf(src);
        int dstIdx = gridEntries.indexOf(dst);

        if(srcIdx == -1 || dstIdx == -1)
            throw new RuntimeException("GridEntry destination or source not part of the Grid");

        gridEntries.get(dstIdx).setPlayer(src.getPlayer());
        gridEntries.get(srcIdx).setPlayer(PlayerNum.NOPLAYER);

        return true;
    }

    @NonNull
    @Override
    public Iterator<GridEntry> iterator() {
        return gridEntries.iterator();
    }
}