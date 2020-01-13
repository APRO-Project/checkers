package com.cyberbot.checkers.game;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Destination {
    public final GridEntry destinationEntry;
    public final ArrayList<GridEntry> capturedPieces;
    public final ArrayList<GridEntry> intermediateSteps;

    Destination(@NotNull CaptureChain captureChain) {
        destinationEntry = captureChain.getLocationAfterCapture();
        capturedPieces = captureChain.getCapturedPieces();
        intermediateSteps = captureChain.getIntermediateSteps();
    }

    Destination(@NotNull GridEntry destinationEntry) {
        this.destinationEntry = destinationEntry;
        capturedPieces = null;
        intermediateSteps = null;
    }

    public boolean isCapture() {
        return capturedPieces == null;
    }
}
