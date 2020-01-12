package com.cyberbot.checkers.game;

import java.util.ArrayList;

public class AiPlayer {
    private PlayerNum aiNum;
    private PlayerNum adversaryNum;
    private Move aiMove;

    public AiPlayer(PlayerNum aiNum) {
        this.aiNum = aiNum;
    }

    public Move getAiMove (Grid currentGrid){
        Tree minmaxTree = generateMinmaxTree(currentGrid);
        return pickMove(minmaxTree);
    }

    private Move pickMove(Tree minmaxTree) {
        return null;//TODO
    }

    private Tree generateMinmaxTree(Grid currentGrid) {
        Tree root = new Tree(currentGrid, currentGrid.getValue(aiNum, adversaryNum), null);
        ArrayList<Move> possibleMoves;
        //TODO
        return root;
    }
}
