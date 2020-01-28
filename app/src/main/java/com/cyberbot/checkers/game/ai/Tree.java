package com.cyberbot.checkers.game.ai;


/**
 * Utility class used to build game tree for needs of {@link AiPlayer}
 */
class Tree {
    private Node root;

    Node getRoot() {
        return root;
    }

    void setRoot(Node root) {
        this.root = root;
    }
}
