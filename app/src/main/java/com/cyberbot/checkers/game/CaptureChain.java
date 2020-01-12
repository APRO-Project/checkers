package com.cyberbot.checkers.game;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    private void getCaptureFinalPositions(@NotNull HashMap<CaptureChain, Integer> output, int captureLength) {
        if(nextCaptures.isEmpty() && lastCapture != null) output.put(this, captureLength);
        for(CaptureChain nextCapture: nextCaptures) {
            nextCapture.getCaptureFinalPositions(output, captureLength+1);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public ArrayList<CaptureChain> getLongestCaptures() {
        ArrayList<CaptureChain> longestCaptures = new ArrayList<>();

        HashMap<CaptureChain, Integer> finalPositions = new HashMap<>();
        getCaptureFinalPositions(finalPositions, 0);

        if(finalPositions.isEmpty()) return longestCaptures;

        final int longestCaptureLength = finalPositions.values().stream().max(Integer::compareTo).get();

        for(Map.Entry<CaptureChain, Integer> finalPosition: finalPositions.entrySet()) {
            if(finalPosition.getValue() == longestCaptureLength) longestCaptures.add(finalPosition.getKey());
        }

        return longestCaptures;
    }
}
