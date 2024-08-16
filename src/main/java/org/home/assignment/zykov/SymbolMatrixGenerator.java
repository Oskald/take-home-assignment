package org.home.assignment.zykov;

import java.util.Map;
import java.util.Random;

class SymbolMatrixGenerator {
    private final Config config;

    private static final Random random = new Random();

    public SymbolMatrixGenerator(Config config) {
        this.config = config;
    }


    public static boolean isStandardSymbol(double bonusSymbolProbability) {
        return random.nextDouble() > bonusSymbolProbability;
    }

    public static String selectSymbol(Map<String, Integer> probabilities) {
        int sumOfProbabilities = probabilities.values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = random.nextInt(sumOfProbabilities) + 1;

        int cumulativeSum = 0;
        for (Map.Entry<String, Integer> entry : probabilities.entrySet()) {
            cumulativeSum += entry.getValue();
            if (randomValue <= cumulativeSum) {
                return entry.getKey();
            }
        }

        throw new IllegalStateException("Невозможно выбрать символ на основе вероятностей");
    }

    public String[][] generateMatrix() {
        String[][] matrix = new String[config.rows][config.columns];

        for (int row = 0; row < config.rows; row++) {
            for (int col = 0; col < config.columns; col++) {
                matrix[row][col] = getRandomSymbol(row, col, config.rows, config.columns);
            }
        }

        return matrix;
    }

    private String getRandomSymbol(int row, int col, int rowNum, int colNum) {
        if(row==rowNum-1 || col==colNum-1) {
            return getRandomBonusSymbol();
        } else {
            return getRandomStandardSymbol(row, col);
        }

    }

    private String getRandomBonusSymbol() {
        Config.BonusSymbols bonus_symbols = config.probabilities.bonus_symbols;
        int sum = totalWeight(bonus_symbols);
        int rand = random.nextInt(sum);
        for (Map.Entry<String, Integer> entry : bonus_symbols.symbols.entrySet()) {
            rand -= entry.getValue();
            if (rand < 0) {
                return entry.getKey();
            }
        }
        return null;

    }

    private Integer totalWeight(Config.StandardSymbol standardSymbol) {
        return standardSymbol.symbols.values().stream().mapToInt(Integer::intValue).sum();
    }

    private Integer totalWeight(Config.BonusSymbols standardSymbol) {
        return standardSymbol.symbols.values().stream().mapToInt(Integer::intValue).sum();
    }


    private String getRandomStandardSymbol(int row, int col) {
        for (Config.StandardSymbol standardSymbol : config.probabilities.standard_symbols) {
            if (standardSymbol.row == row && standardSymbol.column == col) {
                int sum = totalWeight(standardSymbol);
                int rand = random.nextInt(sum);
                for (Map.Entry<String, Integer> entry : standardSymbol.symbols.entrySet()) {
                    rand -= entry.getValue();
                    if (rand < 0) {
                        return entry.getKey();
                    }
                }
            }
        }
        return null;
    }
}