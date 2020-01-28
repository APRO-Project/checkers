package com.cyberbot.checkers.game.logic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.Contract;
import java.io.Serializable;

/**
 * Represents a player piece or a board space, that is unoccupied by none of the players. Stores
 * information about coordinates, owner of the piece and the type of the piece.
 *
 * @see PlayerNum
 * @see PieceType
 */
public class GridEntry implements Serializable {
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

    /**
     * Constructs a new {@link GridEntry} object with given coordinates and {@link GridEntry#player}
     * set to {@link PlayerNum#NOPLAYER} and {@link GridEntry#pieceType} set to {@link PieceType#UNASSIGNED}.
     *
     * @param x x-coordinate of the entry
     * @param y y-coordinate of the entry
     */
    GridEntry(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Tells if the entry is legal, meaning its coordinates point to black space on the board.
     *
     * @return {@code true} if entry is legal, {@code false} otherwise
     */
    public boolean legal() {
        return ((x % 2) ^ (y % 2)) > 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "(" + x + ", " + y + ") - " + player;
    }

    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean equals(@Nullable Object obj) {
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;

        return this.x == ((GridEntry) obj).x && this.y == ((GridEntry) obj).y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}