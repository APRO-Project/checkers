package com.cyberbot.checkers.game;

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

    public boolean checkIfEntryVisited(GridEntry entry) {
        if(getLocationAfterCapture() == entry) return true;

        for(CaptureChain prevCapture = getLastCapture(); prevCapture != null; prevCapture = prevCapture.getLastCapture()) {
            if(prevCapture.getLocationAfterCapture() == entry) return true;
        }

        return false;
    }

    public boolean checkIfEntryCaptured(GridEntry entry) {
        if(getCapturedPiece() == entry) return true;

        for(CaptureChain prevCapture = getLastCapture(); prevCapture != null; prevCapture = prevCapture.getLastCapture()) {
            if(prevCapture.getCapturedPiece() == entry) return true;
        }

        return false;
    }
}
