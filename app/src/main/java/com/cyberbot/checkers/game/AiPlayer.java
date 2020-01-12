package com.cyberbot.checkers.game;

import java.util.ArrayList;

public class AiPlayer {
    private PlayerNum aiNum;
    private PlayerNum adversaryNum;
    private Move aiMove;

    public AiPlayer(PlayerNum aiNum) {
        this.aiNum = aiNum;
    }

    public Move getAiMove(Grid currentGrid) {
        Move bestMove = null;
        int topValue = -1000;
        ArrayList<Move> possibleMoves = null;   //TODO
        for (Move move : possibleMoves) {
            Grid simulated = Grid.simulateMove(currentGrid, move);

            ArrayList<Move> possibleSecondMoves = null;   //TODO
            for (Move move2 : possibleSecondMoves) {
                Grid simulated2 = Grid.simulateMove(simulated, move2);

                ArrayList<Move> possibleThirdMoves = null;   //TODO
                for (Move move3 : possibleThirdMoves) {
                    Grid simulated3 = Grid.simulateMove(simulated2, move3);

                    if (simulated3.getValue(aiNum, adversaryNum) > topValue) {
                        topValue = simulated3.getValue(aiNum, adversaryNum);
                        bestMove = move;
                    }
                }
            }
        }
        return bestMove;
    }
}
