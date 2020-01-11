package com.cyberbot.checkers.game;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashSet;

public class GridEntry {
    private final int x;
    private final int y;
    private PlayerNum player = PlayerNum.NOPLAYER;
    private PieceType pieceType = PieceType.UNASSIGNED;
    private HashSet<GridEntry> allowedMovesCache = null;
    private CaptureChain allowedCapturesCache = null;

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public PlayerNum getPlayer() {
        return player;
    }

    public void setPlayer(PlayerNum player) {
        this.player = player;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    public HashSet<GridEntry> getAllowedMovesCache() {
        return allowedMovesCache;
    }

    public void setAllowedMovesCache(HashSet<GridEntry> allowedMovesCache) {
        this.allowedMovesCache = allowedMovesCache;
    }

    public CaptureChain getAllowedCapturesCache() {
        return allowedCapturesCache;
    }

    public void setAllowedCapturesCache(CaptureChain allowedCapturesCache) {
        this.allowedCapturesCache = allowedCapturesCache;
    }

    public void clearCache() {
        allowedMovesCache = null;
        allowedCapturesCache = null;
    }

    GridEntry(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean legal() {
        return ((x % 2) ^ (y % 2)) > 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "(" + x + ", " + y + ") - " + player;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null)
            return false;
        if(this.getClass() != obj.getClass())
            return false;

        return this.x == ((GridEntry) obj).x && this.y == ((GridEntry) obj).y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}