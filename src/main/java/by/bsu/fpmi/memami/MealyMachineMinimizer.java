package by.bsu.fpmi.memami;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MealyMachineMinimizer {
    public List<Record> minimize(List<Record> records) {
        // Построить начальное разбиение
        Set<Set<Record>> initialSplitting = buildInitialSplitting(records);
        // На его основе задать отношение фи в виде матрицы
        boolean[][] phiRelation = createPhiRelation(records, initialSplitting);
        // Преобразововать матрицу фи до предельного отношения
        Map<Integer, Record> context = createContext(records);
        boolean modified;
        do {
            modified = nextPhiRelation(phiRelation, context);
        } while (modified);
        // Построить конечное разбиение
        Set<Set<Record>> finalSplitting = buildFinalSplitting(new HashSet<>(records), phiRelation, context);
        // Задать автомат по данному разбиению
        return defineMinimizedMachine(finalSplitting);
    }

    private Set<Set<Record>> buildInitialSplitting(List<Record> records) {
        Map<OutputKey, Set<Record>> map = new HashMap<>();
        for (Record record : records) {
            OutputKey key = getOutputKey(record);
            if (map.containsKey(key)) {
                map.get(key).add(record);
            } else {
                Set<Record> set = new HashSet<>();
                set.add(record);
                map.put(key, set);
            }
        }
        return new HashSet<>(map.values());
    }

    private OutputKey getOutputKey(Record record) {
        return new OutputKey(record.getOutputs());
    }

    private boolean[][] createPhiRelation(List<Record> records, Set<Set<Record>> initialSplitting) {
        int dimension = records.size();
        boolean[][] phiRelation = new boolean[dimension][];
        for (int i = 0; i < dimension; i++) {
            phiRelation[i] = new boolean[dimension];
        }
        for (Set<Record> recordSet : initialSplitting) {
            for (Record record : recordSet) {
                int state = record.getState();
                for (Record neighbourRecord : recordSet) {
                    int neighbourState = neighbourRecord.getState();
                    phiRelation[state][neighbourState] = true;
                }
            }
        }
        return phiRelation;
    }

    private Map<Integer, Record> createContext(List<Record> records) {
        Map<Integer, Record> context = new HashMap<>();
        for (Record record : records) {
            context.put(record.getState(), record);
        }
        return context;
    }

    private boolean nextPhiRelation(boolean[][] phiRelation, Map<Integer, Record> context) {
        boolean modified = false;
        int dimension = phiRelation.length;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j <= i; j++) {
                if (phiRelation[i][j] && isClosureViolated(i, j, phiRelation, context)) {
                    phiRelation[i][j] = false;
                    phiRelation[j][i] = false;
                    modified = true;
                }
            }
        }
        return modified;
    }

    private boolean isClosureViolated(int state1, int state2, boolean[][] phiRelation, Map<Integer, Record> context) {
        Record record1 = context.get(state1);
        Record record2 = context.get(state2);
        boolean closureViolated = false;
        Set<String> alphas = record1.getAlphas();
        for (String alpha : alphas) {
            int nextState1 = record1.getTransition(alpha).getNextState();
            int nextState2 = record2.getTransition(alpha).getNextState();
            if (!phiRelation[nextState1][nextState2]) {
                closureViolated = true;
                break;
            }
        }
        return closureViolated;
    }

    private Set<Set<Record>> buildFinalSplitting(Set<Record> records, boolean[][] phiRelation,
                                                 Map<Integer, Record> context) {
        Set<Set<Record>> finalSplitting = new HashSet<>();
        while (!records.isEmpty()) {
            Iterator<Record> iterator = records.iterator();
            Record record = iterator.next();

            Set<Record> set = new HashSet<>();
            set.add(record);
            set = populateNewState(set, phiRelation, context);

            records.removeAll(set);
            finalSplitting.add(set);
        }
        return finalSplitting;
    }

    private Set<Record> populateNewState(Set<Record> records, boolean[][] phiRelation, Map<Integer, Record> context) {
        Set<Record> result = new HashSet<>();
        while (!records.isEmpty()) {
            Iterator<Record> iterator = records.iterator();
            Record record = iterator.next();
            iterator.remove();
            result.add(record);
            int state = record.getState();
            for (int i = 0; i < phiRelation.length; i++) {
                record = context.get(i);
                if (phiRelation[state][i] && !result.contains(record)) {
                    records.add(record);
                }
            }
        }
        return result;
    }

    private List<Record> defineMinimizedMachine(Set<Set<Record>> finalSplitting) {
        List<Record> result = new ArrayList<>();

        Map<Integer, Set<Record>> newStates = new HashMap<>();
        int state = 0;
        for (Set<Record> recordSet : finalSplitting) {
            newStates.put(state++, recordSet);
        }

        for (Map.Entry<Integer, Set<Record>> entry : newStates.entrySet()) {
            state = entry.getKey();
            Set<Record> recordSet = entry.getValue();
            Record record = recordSet.iterator().next();
            Set<String> alphas = record.getAlphas();

            Map<String, Transition> transitions = new HashMap<>();
            for (String alpha : alphas) {
                transitions.put(alpha, getTransition(alpha, record, newStates));
            }

            Map<String, Output> outputs = new HashMap<>();
            for (String alpha : alphas) {
                outputs.put(alpha, new Output(record.getOutput(alpha).getValue()));
            }

            result.add(new Record(state, transitions, outputs));
        }

        return result;
    }

    private Transition getTransition(String alpha, Record record, Map<Integer, Set<Record>> newStates) {
        int nextState = record.getTransition(alpha).getNextState();
        for (Map.Entry<Integer, Set<Record>> entry : newStates.entrySet()) {
            for (Record candidate : entry.getValue()) {
                if (candidate.getState() == nextState) {
                    Transition transition = new Transition();
                    transition.setNextState(entry.getKey());
                    return transition;
                }
            }
        }
        return null;
    }

    private static final class OutputKey implements Serializable {
        private final List<String> outputs;

        public OutputKey(List<String> outputs) {
            this.outputs = Collections.unmodifiableList(outputs);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            OutputKey outputKey = (OutputKey) o;
            return !(outputs != null ? !outputs.equals(outputKey.outputs) : outputKey.outputs != null);
        }

        @Override
        public int hashCode() {
            return outputs != null ? outputs.hashCode() : 0;
        }
    }
}
