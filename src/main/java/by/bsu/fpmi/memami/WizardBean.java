package by.bsu.fpmi.memami;

import org.primefaces.event.FlowEvent;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@ManagedBean
@ViewScoped
public class WizardBean implements Serializable {
    private transient final MealyMachineMinimizer minimizer = new MealyMachineMinimizer();

    private String inputAlphabetString = "a b c";
    private String outputAlphabetString = "x y";
    private int stateCount = 5;

    private List<String> inputAlphabet;
    private List<String> outputAlphabet;

    private List<Record> records;
    private List<Record> resultRecords;

    public String getInputAlphabetString() {
        return inputAlphabetString;
    }

    public void setInputAlphabetString(String inputAlphabetString) {
        this.inputAlphabetString = inputAlphabetString;
    }

    public String getOutputAlphabetString() {
        return outputAlphabetString;
    }

    public void setOutputAlphabetString(String outputAlphabetString) {
        this.outputAlphabetString = outputAlphabetString;
    }

    public int getStateCount() {
        return stateCount;
    }

    public void setStateCount(int stateCount) {
        this.stateCount = stateCount;
    }

    public List<String> getInputAlphabet() {
        return inputAlphabet;
    }

    public List<String> getOutputAlphabet() {
        return outputAlphabet;
    }

    public List<Record> getRecords() {
        return records;
    }

    public String getText() {
        return "Hello, world!";
    }

    public List<String> getStateDescriptors() {
        List<String> stateDescriptors = new ArrayList<>();
        stateDescriptors.add("1");
        stateDescriptors.add("1");
        stateDescriptors.add("1");
        stateDescriptors.add("1");
        stateDescriptors.add("1");
        return stateDescriptors;
    }

    public List<String> getColumns() {
        List<String> columns = new ArrayList<>();
        columns.add("A");
        columns.add("B");
        columns.add("C");
        columns.add("D");
        columns.add("E");
        columns.add("A");
        columns.add("B");
        columns.add("C");
        columns.add("D");
        columns.add("E");
        return columns;
    }

    public String handleFlowEvent(FlowEvent event) {
        String newStep = event.getNewStep();
        String oldStep = event.getOldStep();
        if ("define1".equals(oldStep) && "define2".equals(newStep)) {
            doFirstStep();
        } else if ("define2".equals(oldStep) && "result".equals(newStep)) {
            doSecondStep();
        }
        return event.getNewStep();
    }

    private void doFirstStep() {
        parseTransitions();
        parseOutputs();
        generateRecords();
    }

    private void parseTransitions() {
        String[] tokens = inputAlphabetString.split(" ");
        Set<String> set = new TreeSet<>();
        Collections.addAll(set, tokens);
        inputAlphabet = new ArrayList<>(set);
    }

    private void parseOutputs() {
        String[] tokens = outputAlphabetString.split(" ");
        Set<String> set = new TreeSet<>();
        Collections.addAll(set, tokens);
        outputAlphabet = new ArrayList<>(set);
    }

    private void generateRecords() {
        checkStateCount();
        records = new ArrayList<>();
        for (int state = 0; state < stateCount; state++) {
            Map<String, Transition> transitions = new HashMap<>();
            for (String alpha : this.inputAlphabet) {
                transitions.put(alpha, new Transition());
            }
            Map<String, Output> outputs = new HashMap<>();
            for (String alpha : this.inputAlphabet) {
                outputs.put(alpha, new Output(outputAlphabet.get(0)));
            }
            records.add(new Record(state, transitions, outputs));
        }
    }

    private void checkStateCount() {
        if (stateCount <= 0 && stateCount > 100) {
            stateCount = 5;
        }
    }

    private void doSecondStep() {
        resultRecords = minimizer.minimize(records);
    }
}
