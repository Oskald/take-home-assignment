package org.home.assignment.zykov;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        Map<String, List<String>> winCombinationsSameSymbolMap = getSameSymbolWinCombinationsMap();
        Map<String, String> winHorizontalCombinationsLines = getHorizontalLinesWinCombinationsMap();
        Map<String, String> winVerticalCombinationsLines = getVerticalLinesWinCombinationsMap();
        Map<String, String> winDiagonalCombinationsLines = getDiagonalCombinationLinesMap();
        Map<String, List<String>> appliedWinningCombinations = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : winCombinationsSameSymbolMap.entrySet()) {
            appliedWinningCombinations.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        addLinearCombinations(winHorizontalCombinationsLines, appliedWinningCombinations);
        addLinearCombinations(winVerticalCombinationsLines, appliedWinningCombinations);
        addLinearCombinations(winDiagonalCombinationsLines, appliedWinningCombinations);

        totalReward = (int) calculateRewardForStandardSymbol(appliedWinningCombinations);
        Tuple<String, Config.Symbol> bonusSymbol = getBonusSymbol();
        totalReward = calculateBonuses(totalReward, bonusSymbol.second);

        GameResult gameResult = new GameResult(matrix, totalReward, appliedWinningCombinations, bonusSymbol.first);
        return gameResult;
    }

    private Map<String, String> getDiagonalCombinationLinesMap() {
        boolean leftToRightTheSame = true;
        boolean rightToLeftTheSame = true;
        for (int i = 0; i < config.columns - 1; i++) {

            if (!matrix[i][i].equals(matrix[i+1][i + 1])) {
                leftToRightTheSame = false;
            }
            if (!matrix[i][config.columns - i - 1].equals(matrix[i+1][config.columns - i - 2])) {
                rightToLeftTheSame = false;
            }
            if (!rightToLeftTheSame && !leftToRightTheSame) {
                break;
            }

        }
        Map<String, String> diagonalCombinationLines = new HashMap<>();
        if (leftToRightTheSame) {
            diagonalCombinationLines.put(matrix[0][0], getLeftToRightDiagonalName());
        }
        if (rightToLeftTheSame) {
            diagonalCombinationLines.put(matrix[config.columns-1][0], getRightToLeftDiagonalName());
        }
        return diagonalCombinationLines;
    }

    private static void addLinearCombinations(Map<String, String> winHorizontalCombinationsLines, Map<String, List<String>> appliedWinningCombinations) {
        for (Map.Entry<String, String> entry : winHorizontalCombinationsLines.entrySet()) {
            appliedWinningCombinations.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(entry.getValue());
        }
    }

    private Map<String, String> getHorizontalLinesWinCombinationsMap() {
        int rows = config.rows;
        int columns = config.columns;

        Map<String, String> winCombinationsLines = new HashMap<>();
        String horizontalName = getHorizontalName();
        for (int i = 0; i < rows; i++) {
            boolean theSame = true;
            for (int j = 0; j < columns - 1; j++) {
                if (!matrix[i][j].equals(matrix[i][j + 1])) {
                    theSame = false;
                    break;
                }
            }
            if (theSame) {
                winCombinationsLines.put(matrix[i][0], horizontalName);
            }
        }
        return winCombinationsLines;
    }

    private Map<String, String> getVerticalLinesWinCombinationsMap() {
        int rows = config.rows;
        int columns = config.columns;

        Map<String, String> winCombinationsLines = new HashMap<>();
        String verticalName = getVerticalName();
        for (int i = 0; i < columns; i++) {
            boolean theSame = true;
            for (int j = 0; j < rows - 1; j++) {
                if (!matrix[j][i].equals(matrix[j + 1][i])) {
                    theSame = false;
                    break;
                }
            }
            if (theSame) {
                winCombinationsLines.put(matrix[0][i], verticalName);
            }
        }
        return winCombinationsLines;
    }

    private String getLeftToRightDiagonalName() {
        return config.win_combinations
                .entrySet()
                .stream()
                .filter(it -> it.getValue().when.equals(Config.SameSymbolType.linear_symbols))
                .filter(it -> it.getValue().group.equals(Config.SameSymbolSubType.ltr_diagonally_linear_symbols))
                .map(Map.Entry::getKey)
                .findAny()
                .orElse(null);
    }

    private String getRightToLeftDiagonalName() {
        return config.win_combinations
                .entrySet()
                .stream()
                .filter(it -> it.getValue().when.equals(Config.SameSymbolType.linear_symbols))
                .filter(it -> it.getValue().group.equals(Config.SameSymbolSubType.rtl_diagonally_linear_symbols))
                .map(Map.Entry::getKey)
                .findAny()
                .orElse(null);
    }


    private String getVerticalName() {
        return config.win_combinations
                .entrySet()
                .stream()
                .filter(it -> it.getValue().when.equals(Config.SameSymbolType.linear_symbols))
                .filter(it -> it.getValue().group.equals(Config.SameSymbolSubType.vertically_linear_symbols))
                .map(Map.Entry::getKey)
                .findAny()
                .orElse(null);
    }

    private String getHorizontalName() {
        return config.win_combinations
                .entrySet()
                .stream()
                .filter(it -> it.getValue().when.equals(Config.SameSymbolType.linear_symbols))
                .filter(it -> it.getValue().group.equals(Config.SameSymbolSubType.horizontally_linear_symbols))
                .map(Map.Entry::getKey)
                .findAny()
                .orElse(null);
    }

    private int calculateBonuses(int totalReward, Config.Symbol bonusSymbol) {
        if (bonusSymbol.impact.equals(Config.SymbolImpact.multiply_reward)) {
            totalReward = Double.valueOf(totalReward * bonusSymbol.reward_multiplier).intValue();
        } else if (bonusSymbol.impact.equals(Config.SymbolImpact.extra_bonus)) {
            totalReward += bonusSymbol.extra;
        }
        return totalReward;
    }

    private Tuple<String, Config.Symbol> getBonusSymbol() {
        Map<String, Config.Symbol> bonusSymbols = getBonusSymbols();
        return getBonusSymbol(bonusSymbols);
    }

    private Tuple<String, Config.Symbol> getBonusSymbol(Map<String, Config.Symbol> bonusSymbols) {
        return Arrays.stream(matrix)
                .flatMap(Arrays::stream)
                .map(it -> new Tuple<>(it, bonusSymbols.get(it)))
                .filter(it -> it.second != null)
                .findAny()
                .orElse(null);
    }

    private Map<String, Config.Symbol> getBonusSymbols() {
        return config.symbols
                .entrySet()
                .stream()
                .filter(it -> it.getValue().type.equals(Config.SymbolType.bonus))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private double calculateRewardForStandardSymbol(Map<String, List<String>> winCombinationsSameSymbolMap) {
        double totalReward = 0;
        for (Map.Entry<String, List<String>> entry : winCombinationsSameSymbolMap.entrySet()) {
            String symbol = entry.getKey();
            double localReward = 0;
            List<String> winningCombinations = entry.getValue();
            for (String combination : winningCombinations) {
                double rewardMultiplier = config.win_combinations.get(combination).reward_multiplier;
                localReward = betAmount * rewardMultiplier * config.symbols.get(symbol).reward_multiplier;
                totalReward += (int) localReward;
            }

        }
        return totalReward;
    }

    private Map<String, List<String>> getSameSymbolWinCombinationsMap() {
        Map<String, Long> countMap = getCountMap();

        Map<String, List<Integer>> rewardableCountMap = getRewardableCountMap(countMap);

        return getWinCombinationsMap(rewardableCountMap);
    }

    private Map<String, List<String>> getWinCombinationsMap(Map<String, List<Integer>> rewardableCountMap) {
        Map<String, List<String>> winCombinationsMap = new HashMap<>();
        for (Map.Entry<String, List<Integer>> symbol : rewardableCountMap.entrySet()) {
            List<String> winCombinations = getWinCombinations(symbol);
            winCombinationsMap.put(symbol.getKey(), winCombinations);
        }
        return winCombinationsMap;
    }

    private List<String> getWinCombinations(Map.Entry<String, List<Integer>> symbol) {
        return symbol.getValue()
                .stream()
                .map(it -> config.win_combinations
                        .entrySet()
                        .stream()
                        .filter(winCombination -> winCombination.getValue().when.equals(Config.SameSymbolType.same_symbols))
                        .filter(winCombination -> winCombination.getValue().count == it)
                        .map(Map.Entry::getKey)
                        .findAny()
                        .orElse(null))
                .toList();
    }

    private int getMinWinCombinationsCount() {
        return config.win_combinations
                .values()
                .stream()
                .filter(it -> it.when.equals(Config.SameSymbolType.same_symbols))
                .map(it -> it.count)
                .mapToInt(it -> it)
                .min()
                .orElse(0);
    }

    private Map<String, List<Integer>> getRewardableCountMap(Map<String, Long> countMap) {
        int minWinCombinationsCount = getMinWinCombinationsCount();
        return countMap.entrySet()
                .stream()
                .filter(it -> it.getValue() >= minWinCombinationsCount)
                .collect(Collectors.toMap(Map.Entry::getKey, it -> IntStream.range(minWinCombinationsCount, it.getValue().intValue() + 1)
                        .boxed()
                        .toList()));
    }

    private Map<String, Long> getCountMap() {
        return Arrays.stream(matrix)
                .flatMap(Arrays::stream)
                .collect(Collectors.groupingBy(it -> it, Collectors.counting()));
    }

    private static void printResult(GameResult result) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(result);

        ObjectMapper objectMapper = new ObjectMapper();
        Object json = objectMapper.readValue(jsonString, Object.class);
        ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
        System.out.println(writer.writeValueAsString(json));
    }
}
