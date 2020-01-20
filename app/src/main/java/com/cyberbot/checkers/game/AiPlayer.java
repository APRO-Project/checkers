package com.cyberbot.checkers.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AiPlayer {
    private PlayerNum aiNum;
    private PlayerNum adversaryNum;
    private GridEntry aiMoveSrc;
    private Destination aiMoveDestination;
    private Tree gameTree;
    private int lvl = 3;

    public AiPlayer(PlayerNum aiNum, PlayerNum adversaryNum) {
        this.aiNum = aiNum;
        this.adversaryNum = adversaryNum;
    }

    private void buildTree(Grid rootGrid, int maxDepth){
        gameTree = new Tree();
        Node root = new Node(rootGrid, true, 0);
        gameTree.setRoot(root);
        buildTree(root, maxDepth);
    }

    private void buildTree(Node parentNode, int maxDepth){
        if (parentNode.getDepth()<maxDepth) {
            HashMap<GridEntry, ArrayList<Destination>> possibleMoves;
            if (parentNode.isAiPlayer()) {
                possibleMoves = parentNode.getGrid().getMovableEntries(aiNum);
            } else {
                possibleMoves = parentNode.getGrid().getMovableEntries(adversaryNum);
            }
            for (GridEntry src : possibleMoves.keySet()) {
                for (Destination dst: Objects.requireNonNull(possibleMoves.get(src))){
                    Node newNode = new Node(src, dst, Grid.simulateMove(parentNode.getGrid(), src, dst), !parentNode.isAiPlayer(), parentNode.getDepth()+1);
                    parentNode.addChild(newNode);
                    if (!newNode.getGrid().win(aiNum) && !newNode.getGrid().loose(aiNum)){
                        buildTree(newNode,maxDepth);
                    }
                }
            }
        }
    }

    public HashMap<GridEntry, Destination> getAiMove(Grid grid){
        buildTree(grid, lvl);

    }

    private HashMap<GridEntry, Destination> getTheBestMove(){

    }
}
