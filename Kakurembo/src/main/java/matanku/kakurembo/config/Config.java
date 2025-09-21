package matanku.kakurembo.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Config {

    public static Location LOBBY_LOCATION = Bukkit.getWorld("world").getSpawnLocation(); //todo: remove this from config class because this is not in config.yml
    public static boolean WHITELIST = false;

}
