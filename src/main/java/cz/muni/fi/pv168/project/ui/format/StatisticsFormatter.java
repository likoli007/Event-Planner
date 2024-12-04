package cz.muni.fi.pv168.project.ui.format;

import java.util.HashMap;

public class StatisticsFormatter {
    public static String formatText(HashMap<String, Integer> allStats, HashMap<String, Integer> filteredStats, String key, String label) {
        return formatText(allStats, filteredStats, key, label, null);
    }

    public static String formatText(HashMap<String, Integer> allStats, HashMap<String, Integer> filteredStats, String key, String label, String unit) {
        String unitString = (unit == null) ? "" : " " + unit;

        if (filteredStats == null) {
            int allValue = allStats.get(key);
            return label + ": " + computePadding(allValue) + allValue + unitString;
        } else {
            int filteredValue = filteredStats.get(key);
            int allValue = allStats.get(key);
            return label + ": " + computePadding(filteredValue) + filteredValue + unitString + " (" + allValue + unitString + ")";
        }
    }

    private static String computePadding(int number){
        int maxDigits = 6;
        int count = maxDigits - String.valueOf(number).length();
        if (count > 0)
            return " ".repeat(count);
        return "";
    }

    public static String formatCategoryText(HashMap<String, Integer> allStats, HashMap<String, Integer> filteredStats, String key, String label) {
        if (filteredStats == null) {
            return label + ": " + allStats.get(key);
        }
        else{
            String first = formatCategoryText(allStats.get(key), "all");
            String second = formatCategoryText(filteredStats.get(key), "filtered");
            return label + ": " + String.join(" / ", first, second);
        }
    }

    private static String formatCategoryText(int num, String type){
        return num + " (" + type + ")";
    }

    public static String formatPercentageText(double all, double filtered, String label) {
        if (filtered < 0.0){
            return label + ": " + String.format("%.1f", all) + "%";
        }
        else{
            String first = formatPercentageText(all, "all");
            String second = formatPercentageText(filtered, "filtered");
            return label + ": " + String.join(" / ", first, second);
        }
    }

    private static String formatPercentageText(double num, String type) {
        return String.format("%.1f", num) + "% (" + type + ")";
    }

}
