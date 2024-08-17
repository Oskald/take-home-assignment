package org.home.assignment.zykov;

import java.util.Map;
import java.util.Random;

class SymbolMatrixGenerator {
    private final Config config;

    private static final Random random = new Random();

    public SymbolMatrixGenerator(Config config) {
        this.config = config;
    }


    public String[][] generateMatrix() {
        String[][] matrix = generateStandardSymbols();

        addBonusSymbol(matrix);

        return matrix;
    }

    private String[][] generateStandardSymbols() {
        String[][] matrix = new String[config.rows][config.columns];

        for (int row = 0; row < config.rows; row++) {
            for (int col = 0; col < config.columns; col++) {
                matrix[row][col] = getRandomStandardSymbol(row, col);
            }
        }
        return matrix;
    }

    private void addBonusSymbol(String[][] matrix) {
        int randomRow = random.nextInt(config.rows);
        int randomColumn = random.nextInt(config.columns);

        matrix[randomRow][randomColumn] = getRandomSymbol(config.probabilities.bonus_symbols);
    }


    public String getRandomSymbol(Config.CommonSymbols commonSymbols) {
        int rand = random.nextInt(totalWeight(commonSymbols));
        for (Map.Entry<String, Integer> entry : commonSymbols.symbols.entrySet()) {
            rand -= entry.getValue();
            if (rand < 0) {
                return entry.getKey();
            }
        }
        return null;

    }

    private Integer totalWeight(Config.CommonSymbols standardSymbol) {
        return standardSymbol.symbols.values().stream().mapToInt(Integer::intValue).sum();
    }


    private String getRandomStandardSymbol(int row, int col) {
        for (Config.StandardSymbol standardSymbol : config.probabilities.standard_symbols) {
            if (standardSymbol.row == row && standardSymbol.column == col) {
                return getRandomSymbol(standardSymbol);
            }
        }
        return null;
    }
}