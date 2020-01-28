package com.cyberbot.checkers.game.ai;

import com.cyberbot.checkers.game.logic.Destination;
import com.cyberbot.checkers.game.logic.Grid;
import com.cyberbot.checkers.game.logic.GridEntry;
import com.cyberbot.checkers.game.logic.PlayerNum;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class used to build game tree for needs of {@link AiPlayer}
 */
class Node {
    private GridEntry src;
    private Destination dst;
    private Grid grid;
    private boolean isAiPlayer;
    private final List<Node> children = new ArrayList<>();
    private int depth;
    private int score;

    int getScore() {
        return score;
    }

    void setScore(int score) {
        this.score = score;
    }

    GridEntry getSrc() {
        return src;
    }

    Destination getDst() {
        return dst;
    }

    int getDepth() {
        return depth;
    }

    Grid getGrid() {
        return grid;
    }

    boolean isAiPlayer() {
        return isAiPlayer;
    }

    List<Node> getChildren() {
        return children;
    }

    void addChild(Node node){
        children.add(node);
    }

    Node(Grid grid, boolean isAiPlayer, int depth) {
        this.grid = grid;
        this.isAiPlayer = isAiPlayer;
        this.depth = depth;
    }

    Node(GridEntry src, Destination dst, Grid grid, boolean isAiPlayer, int depth) {
        this.src = src;
        this.dst = dst;
        this.grid = grid;
        this.isAiPlayer = isAiPlayer;
        this.depth = depth;
    }

    int getValue(PlayerNum aiNum, PlayerNum adversaryNum){
        return grid.getValue(aiNum, adversaryNum);
    }

    Node(GridEntry src, Destination dst, int score) {
        this.src = src;
        this.dst = dst;
        this.score = score;
    }
}
