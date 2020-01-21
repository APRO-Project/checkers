package com.cyberbot.checkers.game;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private GridEntry src;
    private Destination dst;
    private Grid grid;
    private boolean isAiPlayer;
    private List<Node> children = new ArrayList<>();
    private int depth;
    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public GridEntry getSrc() {
        return src;
    }

    public void setSrc(GridEntry src) {
        this.src = src;
    }

    public Destination getDst() {
        return dst;
    }

    public void setDst(Destination dst) {
        this.dst = dst;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public boolean isAiPlayer() {
        return isAiPlayer;
    }

    public void setAiPlayer(boolean aiPlayer) {
        isAiPlayer = aiPlayer;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node node){
        children.add(node);
    }

    public Node(Grid grid, boolean isAiPlayer, int depth) {
        this.grid = grid;
        this.isAiPlayer = isAiPlayer;
        this.depth = depth;
    }

    public Node(GridEntry src, Destination dst, Grid grid, boolean isAiPlayer, int depth) {
        this.src = src;
        this.dst = dst;
        this.grid = grid;
        this.isAiPlayer = isAiPlayer;
        this.depth = depth;
    }

    public int getValue(PlayerNum aiNum, PlayerNum adversaryNum){
        return grid.getValue(aiNum, adversaryNum);
    }

    public Node(GridEntry src, Destination dst , int score) {
        this.src = src;
        this.dst = dst;
        this.score = score;
    }
}
