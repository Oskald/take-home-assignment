package org.home.assignment.zykov;

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
        System.out.println(config.columns);
    }



    public static Config fromFile(String configFile) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        configFile = "c:\\Users\\Lenovo\\personal\\test-game\\src\\main\\resources\\config.json";
        Config config = mapper.readValue(new File(configFile), Config.class);
        return config;
    }

    public static class BonusSymbols {
        public Map<String, Integer> symbols;
    }

    public static class Probabilities {
        public List<StandardSymbol> standard_symbols;
        public BonusSymbols bonus_symbols;
    }

    public static class SameSymbol {
        public int reward_multiplier;
        public String when;
        public int count;
        public String group;
        public List<List<String>> covered_areas;
    }

    public static class StandardSymbol {
        public int column;
        public int row;
        public Map<String, Integer> symbols;
    }

    public static class Symbol {
        public int reward_multiplier;
        public int extra;
        public SymbolType type;
        public SymbolImpact impact;
    }

    public enum SymbolImpact {
        multiply_reward, extra_bonus, miss;
    }

    public enum SymbolType {
        standard, bonus;
    }
}
