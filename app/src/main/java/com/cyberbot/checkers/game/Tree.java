package com.cyberbot.checkers.game;

import java.util.ArrayList;

class Tree {
    private Grid gridNode;
    private int boardValue;
    private Move move;
    private ArrayList<Tree> children;

    Tree(Grid gridNode, int boardValue, Move move) {
        this.gridNode = gridNode;
        this.boardValue = boardValue;
        this.move = move;
    }
}
