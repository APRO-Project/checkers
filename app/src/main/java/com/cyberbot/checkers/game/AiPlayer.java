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
        buildTree(root, maxDepth);
    }

    private void buildTree(Node parentNode, int maxDepth) {
        if (parentNode.getDepth() < maxDepth) {
            final HashMap<GridEntry, ArrayList<Destination>> possibleMoves;
            if (parentNode.isAiPlayer()) {
                possibleMoves = parentNode.getGrid().getMovableEntries(aiNum);
            } else {
                possibleMoves = parentNode.getGrid().getMovableEntries(adversaryNum);
            }

            for (GridEntry src : possibleMoves.keySet()) {
                for (Destination dst : possibleMoves.get(src)) {
                    Node newNode = new Node(src, dst,
                            Grid.simulateMove(parentNode.getGrid(), src, dst),
                            !parentNode.isAiPlayer(), parentNode.getDepth() + 1
                    );

                    parentNode.addChild(newNode);
                    if (!newNode.getGrid().won(adversaryNum) && !newNode.getGrid().lost(aiNum)) {
                        buildTree(newNode, maxDepth);
                    }
                }
            }
        }
    }

    public void executeMove(Grid grid) {
        buildTree(grid, lvl);
        if (lvl < 2) {
            for (Node node : gameTree.getRoot().getChildren()) {
                node.setScore(minmax(node, lvl - 1, false));
            }
        } else {
            gameTree.getRoot().setScore(alphaBeta(gameTree.getRoot(), lvl, lvl - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, true));
        }
        final Node bestChild = findBestChild(gameTree.getRoot());
        aiMoveSource = bestChild.getSrc();
        aiMoveDestination = bestChild.getDst();
    }

    private Node findBestChild(Node parent) {
        Node bestNode = new Node(null, null, -999999);
        for (Node child : parent.getChildren()) {
            if (child.getScore() > bestNode.getScore()) {
                bestNode = child;
            }
        }
        return bestNode;
    }

    private int minmax(Node node, int depth, boolean maximizingPlayer) {
        if (depth == 0) {
            return node.getValue(aiNum, adversaryNum);
        } else {
            if (maximizingPlayer) {
                int value = Integer.MIN_VALUE;
                for (Node child : node.getChildren()) {
                    value = Math.max(value, minmax(child, depth - 1, false));
                }
                return value;
            } else {
                int value = Integer.MAX_VALUE;
                for (Node child : node.getChildren()) {
                    value = Math.min(value, minmax(child, depth - 1, true));
                }
                return value;
            }
        }
    }

    private int alphaBeta(Node node, int depth, int lvl2, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0) {
            if (maximizingPlayer) {
                return node.getValue(aiNum, adversaryNum);
            } else {
                return node.getValue(adversaryNum, aiNum);
            }
        } else {
            if (maximizingPlayer) {
                int value = Integer.MIN_VALUE;
                for (Node child : node.getChildren()) {
                    value = Math.max(value, alphaBeta(child, depth - 1, lvl2, alpha, beta, false));
                    alpha = Math.max(alpha, value);
                    if (alpha >= beta) {
                        break;
                    }
                }
                return value;
            } else {
                int value = Integer.MAX_VALUE;
                for (Node child : node.getChildren()) {
                    value = Math.min(value, alphaBeta(child, depth - 1, lvl2, alpha, beta, true));
                    beta = Math.min(beta, value);
                    if (alpha >= beta) {
                        break;
                    }
                }
                if (depth == lvl2) {
                    node.setScore(value);
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
