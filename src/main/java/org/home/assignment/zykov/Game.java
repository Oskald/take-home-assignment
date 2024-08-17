package org.home.assignment.zykov;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Game {
    private final Config config;
    private final int betAmount;

    public Game(Config config, int betAmount) {
        this.config = config;
        this.betAmount = betAmount;
    }

    public static void main(String[] args) {
        try {
            String configFilePath = "c:\\Users\\Lenovo\\personal\\test-game\\src\\main\\resources\\config.json"; // заменить на args[0]
            int betAmount = 100;

            GameResult result = playGame(configFilePath, betAmount);

            printResult(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static GameResult playGame(String configFilePath, int betAmount) throws Exception {
        Config config = Config.fromFile(configFilePath);

        Game game = new Game(config, betAmount);
        return game.play();
    }

    private static void printResult(GameResult result) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(result);

        ObjectMapper objectMapper = new ObjectMapper();
        Object json = objectMapper.readValue(jsonString, Object.class);
        ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
        System.out.println(writer.writeValueAsString(json));
    }

    public GameResult play() {
        String[][] matrix = generateMatrix();


        WinCalculator calculator = new WinCalculator(config, matrix, betAmount);
        GameResult result = calculator.calculate();

        result.setMatrix(matrix);
        return result;
    }

    private String[][] generateMatrix() {
        SymbolMatrixGenerator generator = new SymbolMatrixGenerator(config);
        String[][] matrix = generator.generateMatrix();
        showMatrix(matrix);
        return matrix;
    }

    private static void showMatrix(String[][] matrix) {
        int i = 0;
        int cellWidth = 7;

        System.out.println("╔" + "═".repeat(cellWidth) +
                ("╤" + "═".repeat(cellWidth)).repeat(matrix.length - 1) +
                "╗");

        for (String[] strings : matrix) {
            System.out.println("║ " + Arrays.stream(strings).map(it -> String.format("%-6s", it)).collect(Collectors.joining("│ ")) + "║");
            if (i < matrix.length - 1) {
                ++i;
                System.out.println("╟" + "─".repeat(cellWidth) +
                        ("┼" + "─".repeat(cellWidth)).repeat(matrix.length - 1) +
                        "╢");
            }
        }
        System.out.println("╚" + "═".repeat(cellWidth) +
                ("╧" + "═".repeat(cellWidth)).repeat(matrix.length - 1) +
                "╝");

    }
}
