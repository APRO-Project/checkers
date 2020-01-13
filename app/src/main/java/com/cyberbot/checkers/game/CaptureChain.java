package com.cyberbot.checkers.game;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CaptureChain {
    private final CaptureChain lastCapture;
    private final GridEntry locationAfterCapture;
    private final GridEntry capturedPiece;
    private final Integer captureLength;
    private final ArrayList<CaptureChain> nextCaptures;

    CaptureChain getLastCapture() {
        return lastCapture;
    }

    GridEntry getLocationAfterCapture() {
        return locationAfterCapture;
    }

    GridEntry getCapturedPiece() {
        return capturedPiece;
    }

    Integer getCaptureLength() {
        return captureLength;
    }

    public ArrayList<CaptureChain> getNextCaptures() {
        return nextCaptures;
    }

    void addNextCapture(CaptureChain capture) {
        nextCaptures.add(capture);
    }

    private boolean isCaptureRoot() {
        return capturedPiece == null && lastCapture == null;
    }

    private boolean isCaptureEndpoint() {
        return !isCaptureRoot() && nextCaptures.isEmpty();
    }

    CaptureChain(GridEntry locationAfterCapture, GridEntry capturedPiece, CaptureChain lastCapture) {
        this.locationAfterCapture = locationAfterCapture;
        this.capturedPiece = capturedPiece;
        this.lastCapture = lastCapture;
        nextCaptures = new ArrayList<>();

        if(lastCapture == null) captureLength = 0;
        else captureLength = lastCapture.captureLength + 1;
    }

    boolean checkIfEntryCaptured(GridEntry entry) {
        for(CaptureChain prevCapture = this; prevCapture != null; prevCapture = prevCapture.lastCapture) {
            if(prevCapture.capturedPiece == entry) return true;
        }

        return false;
    }

    private void getCaptureEndpoints(@NotNull ArrayList<CaptureChain> output) {
        if(isCaptureEndpoint()) output.add(this);
        for(CaptureChain nextCapture: nextCaptures) {
            nextCapture.getCaptureEndpoints(output);
        }
    }

    private void getCaptureIntermediateEndpoints(@NotNull ArrayList<CaptureChain> output) {
        if(!isCaptureRoot()) output.add(this);
        for(CaptureChain nextCapture: nextCaptures) {
            nextCapture.getCaptureIntermediateEndpoints(output);
        }
    }

    ArrayList<CaptureChain> getAllCaptures() {
        if(!isCaptureRoot()) {
            throw new RuntimeException("Attempt to get all captures not from capture root");
        }

        ArrayList<CaptureChain> intermediateEndpoints = new ArrayList<>();
        getCaptureIntermediateEndpoints(intermediateEndpoints);

        return intermediateEndpoints;
    }

    ArrayList<CaptureChain> getLongestCaptures() {
        if(!isCaptureRoot()) {
            throw new RuntimeException("Attempt to get longest captures not from capture root");
        }

        ArrayList<CaptureChain> longestCaptures = new ArrayList<>();

        ArrayList<CaptureChain> endpoints = new ArrayList<>();
        getCaptureEndpoints(endpoints);

        if(endpoints.isEmpty()) return longestCaptures;  // Empty list

        final int longestCaptureLength = endpoints.stream().max(
                (a, b) -> a.captureLength.compareTo(b.captureLength)
        ).get().captureLength;

        for(CaptureChain finalPosition: endpoints) {
            if(finalPosition.captureLength == longestCaptureLength) longestCaptures.add(finalPosition);
        }

        return longestCaptures;
    }

    ArrayList<GridEntry> getCapturedPieces() {
        ArrayList<GridEntry> capturedPieces = new ArrayList<>();
        for(CaptureChain capture = this; capture.lastCapture != null; capture = capture.lastCapture) {
            capturedPieces.add(0, capture.capturedPiece);
        }

        return capturedPieces;
    }

    ArrayList<GridEntry> getIntermediateSteps() {
        ArrayList<GridEntry> intermediateSteps = new ArrayList<>();
        for(CaptureChain capture = this.lastCapture; capture.lastCapture != null; capture = capture.lastCapture) {
            intermediateSteps.add(0, capture.locationAfterCapture);
        }

        return intermediateSteps;
    }
}
