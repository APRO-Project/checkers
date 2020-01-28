package com.cyberbot.checkers.game.logic;

public enum GameEndReason {
    // Draws
    DRAW_TOO_MANY_KING_ONLY_MOVES,              // 25 king-only moves without piece moves and captures
    DRAW_NO_MOVABLE_PIECES_REMAINING,           // No more moves to make
    DRAW_KING_VS_KING,                          // King versus King situation

    // Wins
    WIN_OPPONENT_NO_PIECES_REMAINING,           // Opponent has no pieces remaining
    WIN_OPPONENT_NO_MOVABLE_PIECES_REMAINING    // Opponent has no movable pieces remaining
}
