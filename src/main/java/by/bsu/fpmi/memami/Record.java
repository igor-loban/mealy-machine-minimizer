package by.bsu.fpmi.memami;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Record implements Serializable {
    private final int state;
    private final Map<String, Transition> transitions;
    private final Map<String, Output> outputs;

    public Record(int state, Map<String, Transition> transitions, Map<String, Output> outputs) {
        this.state = state;
        this.transitions = new HashMap<>(transitions);
        this.outputs = new HashMap<>(outputs);
    }

    public Set<String> getAlphas() {
        return transitions.keySet();
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

    public List<String> getOutputs() {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Output> entry : outputs.entrySet()) {
            result.add(entry.getValue().getValue());
        }
        return result;
    }
}
