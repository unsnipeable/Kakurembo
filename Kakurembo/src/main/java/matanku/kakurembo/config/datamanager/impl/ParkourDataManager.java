package matanku.kakurembo.config.datamanager.impl;


import matanku.kakurembo.config.datamanager.DataManager;
import matanku.kakurembo.enums.DataEnum;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.DecimalFormat;

public class ParkourDataManager extends DataManager {
    @Override
    public String configName() {
        return "parkour";
    }

    @Override
    public Location loc() {
        return new Location(Bukkit.getWorld("world"), -5.5, -59, -25.5);
    }

    @Override
    public String name() {
        return "Parkour Time";
    }

    @Override
    public String calc(double d) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(d / 20.0) + "s";
    }

    @Override
    public boolean isReverse() {
        return true;
    }

    @Override
    public DataEnum.DataType type() {
        return DataEnum.DataType.INTEGER;
    }

    @Override
    public void setVariable(String playerName, Integer i) {
    }

    @Override
    public void setVariable(String playerName, String s) {

    }
}
