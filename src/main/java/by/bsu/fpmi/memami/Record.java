package by.bsu.fpmi.memami;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Record implements Serializable {
    private final int state;
    private final Map<String, Transition> transitions;
    private final Map<String, Output> outputs;

    public Record(int state, Map<String, Transition> transitions, Map<String, Output> outputs) {
        this.state = state;
        this.transitions = new HashMap<>(transitions);
        this.outputs = new HashMap<>(outputs);
    }

    public int getState() {
        return state;
    }

    public Transition getTransition(String alpha) {
        return transitions.get(alpha);
    }

    public Output getOutput(String alpha) {
        return outputs.get(alpha);
    }
}
