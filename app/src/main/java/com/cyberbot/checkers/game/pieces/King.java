package com.cyberbot.checkers.game.pieces;

import com.cyberbot.checkers.game.GridEntry;
import com.cyberbot.checkers.game.PlayerNum;

import java.util.HashSet;
import java.util.Queue;

public final class King extends Piece implements MoveCalculations {
    public King(GridEntry gridEntry, PlayerNum playerNum) {
        super(gridEntry, playerNum);
    }

    @Override
    public HashSet<GridEntry> calculateAllowedMoves() {
        return null;
    }

    @Override
    public HashSet<Queue<GridEntry>> calculateAllowedCaptures() {
        return null;
    }
}
