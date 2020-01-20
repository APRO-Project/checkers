package com.cyberbot.checkers.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AiPlayer {
    private PlayerNum aiNum;
    private PlayerNum adversaryNum;
    private GridEntry aiMoveSource;
    private Destination aiMoveDestination;
    private Tree gameTree;
    private int lvl = 4;

    public AiPlayer(PlayerNum aiNum, PlayerNum adversaryNum) {
        this.aiNum = aiNum;
        this.adversaryNum = adversaryNum;
    }

    private void buildTree(Grid rootGrid, int maxDepth) {
        gameTree = new Tree();
        Node root = new Node(rootGrid, true, 0);
        gameTree.setRoot(root);
        buildTree(root, maxDepth);
    }

    private void buildTree(Node parentNode, int maxDepth) {
        if (parentNode.getDepth() < maxDepth) {
            HashMap<GridEntry, ArrayList<Destination>> possibleMoves;
            if (parentNode.isAiPlayer()) {
                possibleMoves = parentNode.getGrid().getMovableEntries(aiNum);
            } else {
                possibleMoves = parentNode.getGrid().getMovableEntries(adversaryNum);
            }
            for (GridEntry src : possibleMoves.keySet()) {
                for (Destination dst : Objects.requireNonNull(possibleMoves.get(src))) {
                    Node newNode = new Node(src, dst, Grid.simulateMove(parentNode.getGrid(), src, dst), !parentNode.isAiPlayer(), parentNode.getDepth() + 1);
                    parentNode.addChild(newNode);
                    if (!newNode.getGrid().win(aiNum) && !newNode.getGrid().loose(aiNum)) {
                        buildTree(newNode, maxDepth);
                    }
                }
            }
        }
    }

    public void setAiMove(Grid grid) {
        buildTree(grid, lvl);
        for (Node node : gameTree.getRoot().getChildren()) {
            node.setScore(minmax(node, lvl-1, false));
        }
        aiMoveSource = findBestChild(gameTree.getRoot()).getSrc();
        aiMoveDestination = findBestChild(gameTree.getRoot()).getDst();

    }

    private Node findBestChild(Node parent){
        Node bestNode = new Node(null,null,-999999);
        for (Node child:parent.getChildren()){
            if (child.getScore()>bestNode.getScore()){
                bestNode = child;
            }
        }
        return bestNode;
    }

    private int minmax(Node node, int depth, boolean maximizingPlayer) {
        if (depth == 0) {
            return node.getValue();
        } else {
            if (maximizingPlayer) {
                int value = -999999;
                for (Node child : node.getChildren()) {
                    value = Math.max(value, minmax(child, depth - 1, false));
                }
                return value;
            } else {
                int value = 999999;
                for (Node child : node.getChildren()) {
                    value = Math.min(value, minmax(child, depth - 1, true));
                }
                return value;
            }
        }
    }

    public GridEntry getAiMoveSource() {
        return aiMoveSource;
    }

    public Destination getAiMoveDestination() {
        return aiMoveDestination;
    }
}
