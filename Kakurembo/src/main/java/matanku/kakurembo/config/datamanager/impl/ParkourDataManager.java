package matanku.kakurembo.config.datamanager.impl;


import matanku.kakurembo.config.datamanager.DataManager;
import matanku.kakurembo.config.datamanager.IDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.DecimalFormat;

public class ParkourDataManager extends DataManager {
    @Override
    public String configName() {
        return "data";
    }

    @Override
    public Location loc() {
        return Bukkit.getWorld("world").getBlockAt(15,-56 ,11).getLocation();
    }

    @Override
    public String name() {
        return "Parkour Time";
    }

    @Override
    public String calc(double d) {

        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(d/20.0);
    }
}
