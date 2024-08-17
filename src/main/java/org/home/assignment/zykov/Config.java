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



    public static Config fromFile(String configFile) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(configFile), Config.class);
    }

    public static class CommonSymbols {
        public Map<String, Integer> symbols;
    }

    public static class BonusSymbols extends CommonSymbols{
    }

    public static class Probabilities {
        public List<StandardSymbol> standard_symbols;
        public BonusSymbols bonus_symbols;
    }

    public enum SameSymbolType {
        same_symbols, linear_symbols
    }

    public enum SameSymbolSubType {
        same_symbols,
        horizontally_linear_symbols,
        vertically_linear_symbols,
        ltr_diagonally_linear_symbols,
        rtl_diagonally_linear_symbols
    }

    public static class SameSymbol {
        public double reward_multiplier;
        public SameSymbolType when;
        public int count;
        public SameSymbolSubType group;
        public List<List<String>> covered_areas;
    }

    public static class StandardSymbol extends CommonSymbols {
        public int column;
        public int row;
    }

    public static class Symbol {
        public double reward_multiplier;
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
