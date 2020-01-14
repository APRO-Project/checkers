package com.cyberbot.checkers.game;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GridEntry {
    private final int x;
    private final int y;
    private PlayerNum player = PlayerNum.NOPLAYER;
    private PieceType pieceType = PieceType.UNASSIGNED;

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public PlayerNum getPlayer() {
        return player;
    }

    void setPlayer(PlayerNum player) {
        this.player = player;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
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