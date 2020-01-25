package com.cyberbot.checkers.game;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Represents a tree of all possible captures for a piece. A tree should consist of only one root
 * element with {@link CaptureChain#locationAfterCapture} set to source {@link GridEntry} and
 * the rest of members set to null, zero or empty. The rest of {@link CaptureChain} tree objects
 * store information about possible next captures, reference to the last capture and the length
 * of the capture so far. When the next captures list is empty, the object is considered a capture
 * endpoint.
 */
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

    /**
     * Add capture to {@link CaptureChain#nextCaptures} list.
     *
     * @param capture A capture to be added
     */
    void addNextCapture(CaptureChain capture) {
        nextCaptures.add(capture);
    }

    /**
     * Tell if the capture is the root element of the tree.
     *
     * The root element is the one with {@link CaptureChain#captureLength} equal to zero.
     *
     * @return {@code true} if the capture is the root element, {@code false} otherwise
     */
    @Contract(pure = true)
    private boolean isCaptureRoot() {
        return captureLength == 0;
    }

    /**
     * Tell if the capture is an endpoint of the tree.
     *
     * An endpoint is a non-root capture with empty {@link CaptureChain#nextCaptures} list.
     *
     * @return {@code true} if the capture is an endpoint, {@code false} otherwise
     */
    private boolean isCaptureEndpoint() {
        return !isCaptureRoot() && nextCaptures.isEmpty();
    }

    /**
     * Constructs the root element of capture tree. For details about root element characteristics
     * please see {@link CaptureChain} class description.
     *
     * @param locationAfterCapture Capture source entry
     */
    CaptureChain(@NotNull GridEntry locationAfterCapture) {
        this.locationAfterCapture = locationAfterCapture;
        this.capturedPiece = null;
        this.lastCapture = null;
        this.nextCaptures = new ArrayList<>();
        this.captureLength = 0;
    }

    /**
     * Constructs {@link CaptureChain} object based on given parameters.
     *
     * @param locationAfterCapture {@link GridEntry} at which the piece will land after the capture
     * @param capturedPiece {@link GridEntry} which was captured
     * @param lastCapture Reference to the parent tree element
     */
    CaptureChain(@NotNull GridEntry locationAfterCapture, @NotNull GridEntry capturedPiece, @NotNull CaptureChain lastCapture) {
        this.locationAfterCapture = locationAfterCapture;
        this.capturedPiece = capturedPiece;
        this.lastCapture = lastCapture;
        this.nextCaptures = new ArrayList<>();
        this.captureLength = lastCapture.captureLength + 1;
    }

    /**
     * Tells if given {@code entry} was captured before the current capture.
     *
     * @param entry {@link GridEntry} we want to search for
     * @return {@code true} if {@code entry} was captured, {@code false} otherwise
     */
    boolean checkIfEntryCaptured(GridEntry entry) {
        for(CaptureChain prevCapture = this; prevCapture != null; prevCapture = prevCapture.lastCapture) {
            if(prevCapture.capturedPiece == entry) return true;
        }

        return false;
    }

    /**
     * Get all endpoints of the capture tree.
     *
     * Only root element should call this method and it is the caller responsibility to invoke
     * it properly. No checks are performed since it has recursive nature.
     *
     * Results are saved in the {@code output} parameter.
     *
     * @param output List of capture tree endpoints
     */
    private void getCaptureEndpoints(@NotNull ArrayList<CaptureChain> output) {
        if(isCaptureEndpoint()) output.add(this);
        for(CaptureChain nextCapture: nextCaptures) {
            nextCapture.getCaptureEndpoints(output);
        }
    }

    /**
     * Get all intermediate endpoint of the capture tree.
     *
     * An intermediate endpoint is any position in the capture tree that is not root.
     *
     * Only root element should call this method and it is the caller responsibility to invoke
     * it properly. No checks are performed since it has recursive nature.
     *
     * Results are saved in the {@code output} parameter.
     *
     * @param output List of capture tree intermediate endpoints
     */
    private void getCaptureIntermediateEndpoints(@NotNull ArrayList<CaptureChain> output) {
        if(!isCaptureRoot()) output.add(this);
        for(CaptureChain nextCapture: nextCaptures) {
            nextCapture.getCaptureIntermediateEndpoints(output);
        }
    }

    /**
     * Wrapper around {@link CaptureChain#getCaptureIntermediateEndpoints(ArrayList)} that ensures
     * root element is the caller.
     *
     * @return List of all possible captures in the capture tree
     */
    @NotNull
    ArrayList<CaptureChain> getAllCaptures() {
        if(!isCaptureRoot()) {
            throw new RuntimeException("Attempt to get all captures not from capture root");
        }

        ArrayList<CaptureChain> intermediateEndpoints = new ArrayList<>();
        getCaptureIntermediateEndpoints(intermediateEndpoints);

        return intermediateEndpoints;
    }

    /**
     * Gets the longest captures based on {@link CaptureChain#captureLength} from the capture tree
     * endpoints. Only root can call this method as it utilizes {@link CaptureChain#getCaptureEndpoints(ArrayList)}.
     *
     * @return List of all longest captures in the capture tree
     */
    @NotNull
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

    /**
     * Get all captured pieces so far.
     *
     * @return List of all captured pieces so far
     */
    @NotNull
    ArrayList<GridEntry> getCapturedPieces() {
        ArrayList<GridEntry> capturedPieces = new ArrayList<>();
        for(CaptureChain capture = this; capture.lastCapture != null; capture = capture.lastCapture) {
            capturedPieces.add(0, capture.capturedPiece);
        }

        return capturedPieces;
    }

    /**
     * Get all intermediate steps performed so far.
     *
     * @return List of all intermediate steps performed so far
     */
    ArrayList<GridEntry> getIntermediateSteps() {
        ArrayList<GridEntry> intermediateSteps = new ArrayList<>();

        if(lastCapture == null) return intermediateSteps;  // Empty list if called from root

        for(CaptureChain capture = this.lastCapture; capture.lastCapture != null; capture = capture.lastCapture) {
            intermediateSteps.add(0, capture.locationAfterCapture);
        }

        return intermediateSteps;
    }
}
