package com.cyberbot.checkers.game;

import org.jetbrains.annotations.NotNull;

public class CaptureTree {
    private final Capture root;

    public CaptureTree(GridEntry rootEntry) {
        this.root = new Capture(rootEntry, null, null);
    }

    public void addCapture(@NotNull Capture capture, @NotNull Capture nextCapture) {
        capture.getNextCaptures().add(nextCapture);
    }
}
