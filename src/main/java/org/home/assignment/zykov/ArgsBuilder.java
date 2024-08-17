package org.home.assignment.zykov;

public class ArgsBuilder {
    public static Tuple<String, Integer> getArgs(String[] args) {
        String configFile = null;
        Integer betAmount = null;
        for (int i = 0; i < args.length; i++) {
            if(args[i].equals("--config")) {
                if(i< args.length-1) {
                    configFile = args[i+1];
                }
            }
            if(args[i].equals("--betAmount")) {
                if(i< args.length-1) {
                    betAmount = Integer.parseInt(args[i+1]);
                }
            }
        }
        return new Tuple<>(configFile, betAmount);
    }
}
