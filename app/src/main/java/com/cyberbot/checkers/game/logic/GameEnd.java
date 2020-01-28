package com.cyberbot.checkers.game.logic;

/**
 * Stores information about how game has ended.
 *
 * {@link GameEnd#winner} has value of {@link PlayerNum#NOPLAYER} when there was
 * a draw situation.
 *
 * {@link GameEnd#reason} tells something more about what exactly happen that cause
 * the game to end.
 *
 * @see GameEndReason
 */
public class GameEnd {
    private final PlayerNum winner;
    private final GameEndReason reason;

    public PlayerNum getWinner() {
        return winner;
    }

    public GameEndReason getReason() {
        return reason;
    }

    GameEnd(PlayerNum winner, GameEndReason reason) {
        this.winner = winner;
        this.reason = reason;
    }
}
