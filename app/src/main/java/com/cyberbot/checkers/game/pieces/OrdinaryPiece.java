package com.cyberbot.checkers.game.pieces;

import com.cyberbot.checkers.game.GridEntry;
import com.cyberbot.checkers.game.PlayerNum;

import java.util.HashSet;
import java.util.Queue;

// First player color - WHITE
// First player position - UP

public final class OrdinaryPiece extends Piece implements MoveCalculations {
    public OrdinaryPiece(GridEntry gridEntry, PlayerNum playerNum) {
        super(gridEntry, playerNum);
    }

    public King promote() {
        // TODO: Check if current location permits promotion
        return new King(location, player);
    }

    @Override
    public HashSet<GridEntry> calculateAllowedMoves() {
        HashSet<GridEntry> allowedMoves = new HashSet<>();
        for(GridEntry entry: getAdjacentEntries()) {
            if(entry.getPlayer() == PlayerNum.NOPLAYER) {
                if(prefs.getCanMoveBackwards()
                        || (player == PlayerNum.FIRST && location.getY() < entry.getY())
                        || (player == PlayerNum.SECOND && location.getY() > entry.getY())) {
                    allowedMoves.add(entry);
                }
            }
        }

        return allowedMoves;
    }

    @Override
    public HashSet<Queue<GridEntry>> calculateAllowedCaptures() {
        return null;
    }
}
