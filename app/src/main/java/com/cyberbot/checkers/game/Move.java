package com.cyberbot.checkers.game;

import java.util.ArrayList;

public class Move {
    private GridEntry start;
    private GridEntry end;
    private ArrayList<GridEntry> killedInAction;

    public Move(GridEntry start, GridEntry end) {
        this.start = start;
        this.end = end;
        this.killedInAction = null;
    }

    public Move(GridEntry start, GridEntry end, ArrayList<GridEntry> killedInAction) {
        this.start = start;
        this.end = end;
        this.killedInAction = killedInAction;
    }

    public GridEntry getStart() {
        return start;
    }

    public GridEntry getEnd() {
        return end;
    }

    public ArrayList<GridEntry> getKilledInAction() {
        return killedInAction;
    }
}
