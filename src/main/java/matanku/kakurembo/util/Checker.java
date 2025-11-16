package matanku.kakurembo.util;

import matanku.kakurembo.util.enums.RankEnum;

public class Checker {
    public static boolean isInteger(String index) {
        try {
            Integer.parseInt(index);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isBoolean(String index) {
        return index.equals("true") || index.equals("false");
    }

    public static boolean isRank(String index) {
        try {
            RankEnum rank = RankEnum.valueOf(index);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
