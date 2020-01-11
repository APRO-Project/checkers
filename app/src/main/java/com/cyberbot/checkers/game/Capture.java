package com.cyberbot.checkers.game;

import java.util.ArrayList;

public class Capture {
    private final Capture lastCapture;
    private final GridEntry locationAfterCapture;
    private final GridEntry capturedPiece;
    private final ArrayList<Capture> nextCaptures;

    public Capture getLastCapture() {
        return lastCapture;
    }

    public GridEntry getLocationAfterCapture() {
        return locationAfterCapture;
    }

    public GridEntry getCapturedPiece() {
        return capturedPiece;
    }

    public ArrayList<Capture> getNextCaptures() {
        return nextCaptures;
    }

    Capture(GridEntry locationAfterCapture, GridEntry capturedPiece, Capture lastCapture) {
        this.locationAfterCapture = locationAfterCapture;
        this.capturedPiece = capturedPiece;
        this.lastCapture = lastCapture;
        nextCaptures = new ArrayList<>();
    }
}
