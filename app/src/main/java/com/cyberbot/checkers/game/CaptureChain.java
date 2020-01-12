package com.cyberbot.checkers.game;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CaptureChain {
    private final CaptureChain lastCapture;
    private final GridEntry locationAfterCapture;
    private final GridEntry capturedPiece;
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

    public ArrayList<CaptureChain> getNextCaptures() {
        return nextCaptures;
    }

    public void addNextCapture(CaptureChain capture) {
        nextCaptures.add(capture);
    }

    CaptureChain(GridEntry locationAfterCapture, GridEntry capturedPiece, CaptureChain lastCapture) {
        this.locationAfterCapture = locationAfterCapture;
        this.capturedPiece = capturedPiece;
        this.lastCapture = lastCapture;
        nextCaptures = new ArrayList<>();
    }

    public boolean checkIfEntryCaptured(GridEntry entry) {
        if(getCapturedPiece() == entry) return true;

        for(CaptureChain prevCapture = getLastCapture(); prevCapture != null; prevCapture = prevCapture.getLastCapture()) {
            if(prevCapture.getCapturedPiece() == entry) return true;
        }

        return false;
    }

    public void getCaptureFinalPositions(@NotNull ArrayList<CaptureChain> output) {
        if(nextCaptures.isEmpty() && lastCapture != null) output.add(this);
        for(CaptureChain nextCapture: nextCaptures) {
            nextCapture.getCaptureFinalPositions(output);
        }
    }
}
