package matanku.kakurembo.config.datamanager.impl;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.config.datamanager.DataManager;
import matanku.kakurembo.enums.DataEnum;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class TrollDataManager extends DataManager {
    @Override
    public String configName() {
        return "troll";
    }

    @Override
    public Location loc() {
        return new Location(Bukkit.getWorld("world"), 7.0,-59,-6.0);
    }


    @Override
    public String name() {
        return "Troll Point";
    }

    @Override
    public String calc(double d) {
        return (int)d + "";
    }

    @Override
    public DataEnum.DataType type() {
        return DataEnum.DataType.INTEGER;
    }

    @Override
    public void setVariable(String playerName, Integer i) {
        HideAndSeek.getInstance().getGame().getGamePlayer(UUID.fromString(playerName)).setTrollPoint(i);
    }

    @Override
    public void setVariable(String playerName, String s) {

    }
}
