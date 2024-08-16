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

            System.out.println("Phase 0");
            Game game = new Game(config, betAmount);
            GameResult result = game.play();

            System.out.println("Phase 4");
            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writeValueAsString(result));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GameResult play() {
        System.out.println("Phase 1");
        SymbolMatrixGenerator generator = new SymbolMatrixGenerator(config);
        String[][] matrix = generator.generateMatrix();

        System.out.println("Phase 2");
        WinCalculator calculator = new WinCalculator(config, matrix, betAmount);
        GameResult result = calculator.calculate();

        System.out.println("Phase 3");
        result.setMatrix(matrix);
        return result;
    }
}
