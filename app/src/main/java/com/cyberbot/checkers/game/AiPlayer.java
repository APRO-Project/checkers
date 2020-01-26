package com.cyberbot.checkers.game;

import java.util.ArrayList;
import java.util.HashMap;

public class AiPlayer {
    private final PlayerNum aiNum;
    private final PlayerNum adversaryNum;
    private GridEntry aiMoveSource;
    private Destination aiMoveDestination;
    private Tree gameTree;
    private final int lvl;

    public AiPlayer(PlayerNum aiNum, PlayerNum adversaryNum, int lvl) {
        this.aiNum = aiNum;
        this.adversaryNum = adversaryNum;
        this.lvl = lvl;
    }

    private void buildTree(Grid rootGrid, int maxDepth) {
        gameTree = new Tree();
        Node root = new Node(rootGrid, true, 0);
        gameTree.setRoot(root);
    }

    public void executeMove(Grid grid) {
        buildTree(grid, lvl);
        gameTree.getRoot().setScore(alphaBeta(gameTree.getRoot(), lvl, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, true));
        final Node bestChild = findBestChild(gameTree.getRoot());
        aiMoveSource = bestChild.getSrc();
        aiMoveDestination = bestChild.getDst();
    }

    private Node findBestChild(Node parent) {
        Node bestNode = new Node(null, null, Integer.MIN_VALUE);
        for (Node child : parent.getChildren()) {
            if (child.getScore() > bestNode.getScore()) {
                bestNode = child;
            }
        }
        return bestNode;
    }

    private int alphaBeta(Node node, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0) {
            if (maximizingPlayer) {
                int value = node.getValue(aiNum, adversaryNum);
                node.setScore(value);
                return value;
            } else {
                int value = node.getValue(adversaryNum, aiNum);
                node.setScore(value);
                return value;
            }
        } else {

            final HashMap<GridEntry, ArrayList<Destination>> possibleMoves;
            if (node.isAiPlayer()) {
                possibleMoves = node.getGrid().getMovableEntries(aiNum);
            } else {
                possibleMoves = node.getGrid().getMovableEntries(adversaryNum);
            }

            for (GridEntry src : possibleMoves.keySet()) {
                for (Destination dst : possibleMoves.get(src)) {
                    Node newNode = new Node(src, dst,
                            Grid.simulateMove(node.getGrid(), src, dst),
                            !node.isAiPlayer(), node.getDepth() - 1
                    );

                    node.addChild(newNode);
                }
            }

            if (maximizingPlayer) {
                int value = Integer.MIN_VALUE + 1;
                for (Node child : node.getChildren()) {
                    value = Math.max(value, alphaBeta(child, depth - 1, alpha, beta, false));
                    alpha = Math.max(alpha, value);
                    if (alpha >= beta) {
                        break;
                    }
                }
                node.setScore(value);
                return value;
            } else {
                int value = Integer.MAX_VALUE - 1;
                for (Node child : node.getChildren()) {
                    value = Math.min(value, alphaBeta(child, depth - 1, alpha, beta, true));
                    beta = Math.min(beta, value);
                    if (alpha >= beta) {
                        break;
                    }
                }
                node.setScore(value);
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
