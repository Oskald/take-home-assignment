package org.home.assignment.zykov.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Config {
    public int columns;
    public int rows;
    public Map<String, Symbol> symbols;
    public Probabilities probabilities;
    public  Map<String, SameSymbol> win_combinations;


    public static void main(String[] args) {
        Config config = null;
        try {
            config = fromFile("");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(config.symbols);
    }



    public static Config fromFile(String configFile) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        configFile = "c:\\Users\\Lenovo\\personal\\test-game\\src\\main\\resources\\config.json";
        return mapper.readValue(new File(configFile), Config.class);
    }
}
