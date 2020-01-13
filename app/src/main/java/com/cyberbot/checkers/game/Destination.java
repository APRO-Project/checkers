package com.cyberbot.checkers.game;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Destination {
    private final GridEntry destinationEntry;
    private final ArrayList<GridEntry> capturedPieces;
    private final ArrayList<GridEntry> intermediateSteps;

    Destination(@NotNull CaptureChain captureChain) {
        destinationEntry = captureChain.getLocationAfterCapture();
        capturedPieces = captureChain.getCapturedPieces();
        intermediateSteps = captureChain.getIntermediateSteps();
    }

    Destination(@NotNull GridEntry destinationEntry) {
        this.destinationEntry = destinationEntry;
        capturedPieces = new ArrayList<>();
        intermediateSteps = new ArrayList<>();
    }

    public boolean isCapture() {
        return capturedPieces != null && capturedPieces.size() > 0;
    }

    public ArrayList<GridEntry> getCapturedPieces() {
        return capturedPieces;
    }

    public GridEntry getDestinationEntry() {
        return destinationEntry;
    }

    public ArrayList<GridEntry> getIntermediateSteps() {
        return intermediateSteps;
    }
}
