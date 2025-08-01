package matanku.kakurembo.api.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;

public class HeadUtil {

    public static String getValue(Player player) {
        if (player != null) {
            GameProfile gameProfile = ((CraftPlayer)player).getHandle().getGameProfile();

            for (Map.Entry<String, Property> entry : gameProfile.getProperties().entries()) {
                return entry.getValue().value();
            }
        }

        return null;
    }

    public static String[] getValues(Player player) {
        return getValues(null, player);
    }

    public static String[] getValues(String key, Player player) {
        if (player != null) {
            GameProfile gameProfile = ((CraftPlayer)player).getHandle().getGameProfile();

            for (Map.Entry<String, Property> entry : gameProfile.getProperties().entries()) {
                if (key == null || entry.getKey().equals(key)) {
                    return new String[]{entry.getValue().value(), entry.getValue().signature()};
                }
            }
        }

        return null;
    }

}
