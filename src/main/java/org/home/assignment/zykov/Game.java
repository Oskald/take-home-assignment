package org.home.assignment.zykov;

import com.fasterxml.jackson.databind.ObjectMapper;

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
            Config config = Config.fromFile(configFilePath);

            Game game = new Game(config, betAmount);
            GameResult result = game.play();

            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writeValueAsString(result));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GameResult play() {
        SymbolMatrixGenerator generator = new SymbolMatrixGenerator(config);
        String[][] matrix = generator.generateMatrix();

        WinCalculator calculator = new WinCalculator(config, matrix, betAmount);
        GameResult result = calculator.calculate();

        result.setMatrix(matrix);
        return result;
    }
}
