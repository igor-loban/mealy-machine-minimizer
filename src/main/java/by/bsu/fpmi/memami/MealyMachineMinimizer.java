package by.bsu.fpmi.memami;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
        Set<Set<Record>> finalSplitting = buildFinalSplitting(records, phiRelation);
        // Задать автомат по данному разбиению
        return defineMinimizedMachine(records, finalSplitting);
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

    private boolean isClosureViolated(int i, int j, boolean[][] phiRelation, Map<Integer, Record> context) {
        return false;
    }

    private Set<Set<Record>> buildFinalSplitting(List<Record> records, boolean[][] phiRelation) {
        return null;
    }

    private List<Record> defineMinimizedMachine(List<Record> records, Set<Set<Record>> finalSplitting) {
        return records; // TODO: implement
    }

    private static final class OutputKey {
        private final List<String> outputs;

        public OutputKey(List<String> outputs) {
            this.outputs = Collections.unmodifiableList(outputs);
        }

        public List<String> getOutputs() {
            return outputs;
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

            if (outputs != null ? !outputs.equals(outputKey.outputs) : outputKey.outputs != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return outputs != null ? outputs.hashCode() : 0;
        }
    }
}
