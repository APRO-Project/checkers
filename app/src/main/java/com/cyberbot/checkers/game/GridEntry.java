package com.cyberbot.checkers.game;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GridEntry {
    private int y;
    private int x;
    private PlayerNum player = PlayerNum.NOPLAYER;

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public PlayerNum getPlayer() {
        return player;
    }

    public void setPlayer(PlayerNum player) {
        this.player = player;
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