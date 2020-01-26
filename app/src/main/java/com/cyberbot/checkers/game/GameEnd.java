package com.cyberbot.checkers.game;

import org.jetbrains.annotations.NotNull;

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
