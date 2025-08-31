package matanku.kakurembo.config.datamanager.impl;


import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.config.datamanager.DataManager;
import matanku.kakurembo.config.datamanager.IDataManager;
import matanku.kakurembo.enums.DataManagerType;
import matanku.kakurembo.enums.DataType;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.DecimalFormat;
import java.util.UUID;

public class ParkourDataManager extends DataManager {
    @Override
    public String configName() {
        return "parkour";
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
        return df.format(d / 20.0) + "s";
    }

    @Override
    public boolean isReverse() {
        return true;
    }

    @Override
    public DataType type() {
        return DataType.INTEGER;
    }

    @Override
    public void setVariable(String playerName, Integer i) {
    }

    @Override
    public void setVariable(String playerName, String s) {

    }
}
