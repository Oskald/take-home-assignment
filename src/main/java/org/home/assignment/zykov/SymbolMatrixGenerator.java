package org.home.assignment.zykov;

import java.util.Map;
import java.util.Random;

class SymbolMatrixGenerator {
    private final Config config;
    private final Random random = new Random();

    public SymbolMatrixGenerator(Config config) {
        this.config = config;
    }

    public String[][] generateMatrix() {
        String[][] matrix = new String[config.rows][config.columns];

        for (int row = 0; row < config.rows; row++) {
            for (int col = 0; col < config.columns; col++) {
                matrix[row][col] = generateSymbol(row, col);
            }
        }

        return matrix;
    }

    private String generateSymbol(int row, int col) {
        Map<String, Integer> symbols = config.probabilities.standard_symbols.get(row).symbols;
        int totalProbability = symbols.values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = random.nextInt(totalProbability);

        int cumulativeProbability = 0;
        for (Map.Entry<String, Integer> entry : symbols.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (randomValue < cumulativeProbability) {
                return entry.getKey();
            }
        }
//for error case
        return null;
    }
}