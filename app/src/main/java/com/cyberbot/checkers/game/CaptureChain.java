package com.cyberbot.checkers.game;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CaptureChain {
    private final CaptureChain lastCapture;
    private final GridEntry locationAfterCapture;
    private final GridEntry capturedPiece;
    private final Integer captureLength;
    private final ArrayList<CaptureChain> nextCaptures;

    public CaptureChain getLastCapture() {
        return lastCapture;
    }

    public GridEntry getLocationAfterCapture() {
        return locationAfterCapture;
    }

    public GridEntry getCapturedPiece() {
        return capturedPiece;
    }

    public Integer getCaptureLength() {
        return captureLength;
    }

    public ArrayList<CaptureChain> getNextCaptures() {
        return nextCaptures;
    }

    public void addNextCapture(CaptureChain capture) {
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

    public boolean checkIfEntryCaptured(GridEntry entry) {
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

    public CaptureChain getCaptureRoot() {
        if(isCaptureRoot()) return this;

        CaptureChain previousCapture = this;
        while(!previousCapture.isCaptureRoot()) {
            previousCapture = previousCapture.lastCapture;
        }

        return previousCapture;
    }

    public ArrayList<CaptureChain> getLongestCaptures() {
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

    public ArrayList<GridEntry> getCapturedPieces() {
        if(!isCaptureEndpoint()) {
            throw new RuntimeException("Attempt to get captured pieces not from capture endpoint");
        }

        ArrayList<GridEntry> capturedPieces = new ArrayList<>();
        for(CaptureChain capture = this; capture.lastCapture != null; capture = capture.lastCapture) {
            capturedPieces.add(capture.capturedPiece);
        }

        return capturedPieces;
    }
}
