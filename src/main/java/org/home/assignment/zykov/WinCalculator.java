package org.home.assignment.zykov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class WinCalculator {
    private final Config config;
    private final String[][] matrix;
    private final int betAmount;

    public WinCalculator(Config config, String[][] matrix, int betAmount) {
        this.config = config;
        this.matrix = matrix;
        this.betAmount = betAmount;
    }

    public GameResult calculate() {
        int totalReward = 0;
        Map<String, List<String>> appliedWinningCombinations = new HashMap<>();
        String appliedBonusSymbol = null;

        // 1. Проверка выигрышных комбинаций
        for (Map.Entry<String, Config.SameSymbol> entry : config.win_combinations.entrySet()) {
            String combinationName = entry.getKey();
            Config.SameSymbol winCondition = entry.getValue();

            if (winCondition.when.equals("same_symbols")) {
                totalReward += processSameSymbols(winCondition, combinationName, appliedWinningCombinations);
            } else if (winCondition.when.equals("linear_symbols")) {
                totalReward += processLinearSymbols(winCondition, combinationName, appliedWinningCombinations);
            }
        }

        // 2. Применение бонусных символов
        appliedBonusSymbol = applyBonusSymbols(totalReward);
        if (appliedBonusSymbol != null) {
            Config.Symbol bonus = config.symbols.get(appliedBonusSymbol);
            if (bonus.impact == Config.SymbolImpact.multiply_reward) {
                totalReward *= bonus.reward_multiplier;
            } else if (bonus.impact == Config.SymbolImpact.extra_bonus) {
                totalReward += bonus.extra;
            }
            // Если бонус `MISS`, он не оказывает эффекта и игнорируется
        }

        return new GameResult(matrix, totalReward, appliedWinningCombinations, appliedBonusSymbol);
    }

    private int processSameSymbols(Config.SameSymbol winCondition, String combinationName, Map<String, List<String>> appliedWinningCombinations) {
        Map<String, Integer> symbolCounts = new HashMap<>();

        // Подсчет количества каждого символа в матрице
        for (int row = 0; row < config.rows; row++) {
            for (int col = 0; col < config.columns; col++) {
                String symbol = matrix[row][col];
                symbolCounts.put(symbol, symbolCounts.getOrDefault(symbol, 0) + 1);
            }
        }

        int totalReward = 0;

        // Проверка, если количество какого-либо символа соответствует выигрышному условию
        for (Map.Entry<String, Integer> entry : symbolCounts.entrySet()) {
            String symbol = entry.getKey();
            int count = entry.getValue();

            if (count >= winCondition.count) {
                int reward = betAmount * config.symbols.get(symbol).reward_multiplier * winCondition.reward_multiplier;
                totalReward += reward;

                appliedWinningCombinations.computeIfAbsent(symbol, k -> new ArrayList<>()).add(combinationName);
            }
        }

        return totalReward;
    }

    private int processLinearSymbols(Config.SameSymbol winCondition, String combinationName, Map<String, List<String>> appliedWinningCombinations) {
        int totalReward = 0;

        for (List<String> area : winCondition.covered_areas) {
            String firstSymbol = null;
            boolean isWinning = true;

            for (String position : area) {
                String[] pos = position.split(":");
                int row = Integer.parseInt(pos[0]);
                int col = Integer.parseInt(pos[1]);
                String symbol = matrix[row][col];

                if (firstSymbol == null) {
                    firstSymbol = symbol;
                } else if (!symbol.equals(firstSymbol)) {
                    isWinning = false;
                    break;
                }
            }

            if (isWinning && firstSymbol != null) {
                int reward = betAmount * config.symbols.get(firstSymbol).reward_multiplier * winCondition.reward_multiplier;
                totalReward += reward;

                appliedWinningCombinations.computeIfAbsent(firstSymbol, k -> new ArrayList<>()).add(combinationName);
            }
        }

        return totalReward;
    }

    private String applyBonusSymbols(int reward) {
        for (int row = 0; row < config.rows; row++) {
            for (int col = 0; col < config.columns; col++) {
                String symbol = matrix[row][col];
                if (config.symbols.containsKey(symbol) && config.symbols.get(symbol).type == Config.SymbolType.bonus) {
                    return symbol;
                }
            }
        }
        return null;
    }
}

