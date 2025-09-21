package matanku.kakurembo.api.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Checker {

    public static boolean isPluginEnabled(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null;
    }

    public static boolean isInteger(String index) {
        try {
            Integer.parseInt(index);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isDouble(String index) {
        try {
            Double.parseDouble(index);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isFloat(String index) {
        try {
            Float.parseFloat(index);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isBoolean(String index) {
        return index.equals("true") || index.equals("false");
    }

    public static boolean isUUID(String index) {
        try {
            UUID.fromString(index);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isMaterial(String index) {
        try {
            Material.valueOf(index.toUpperCase());
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isPotionEffect(String index) {
        try {
            PotionEffectType.getByName(index);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isClassExists(String string) {
        try {
            Class.forName(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
