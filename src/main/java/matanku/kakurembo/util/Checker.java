package matanku.kakurembo.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

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

}
