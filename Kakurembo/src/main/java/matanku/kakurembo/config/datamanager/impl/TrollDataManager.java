package matanku.kakurembo.config.datamanager.impl;

import matanku.kakurembo.config.datamanager.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class TrollDataManager extends DataManager {
    @Override
    public String configName() {
        return "troll";
    }

    @Override
    public Location loc() {
        return Bukkit.getWorld("world").getBlockAt(15,-56 ,16).getLocation();
    }


    @Override
    public String name() {
        return "Troll Point";
    }

    @Override
    public String calc(double d) {
        return (int)d + "";
    }
}
