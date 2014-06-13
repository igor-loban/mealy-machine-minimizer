package by.bsu.fpmi.memami;

import java.io.Serializable;

public class Transition implements Serializable {
    private int nextState;

    public int getNextState() {
        return nextState;
    }

    public void setNextState(int nextState) {
        this.nextState = nextState;
    }
}
