package com.cyberbot.checkers.game;

import org.jetbrains.annotations.NotNull;

/**
 * Stores information about how game has ended.
 *
 * {@link GameEnd#winner} has value of {@link PlayerNum#NOPLAYER} when there was
 * a draw situation.
 *
 * {@link GameEnd#reason} tells something more about what exactly happen that cause
 * the game to end.
 */
public class GameEnd {
    private final PlayerNum winner;
    private final String reason;

    public PlayerNum getWinner() {
        return winner;
    }

    public String getReason() {
        return reason;
    }

    GameEnd(PlayerNum winner, @NotNull String reason) {
        this.winner = winner;
        this.reason = reason;
    }
}
