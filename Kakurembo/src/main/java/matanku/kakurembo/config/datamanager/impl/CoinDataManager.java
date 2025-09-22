package matanku.kakurembo.config.datamanager.impl;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.config.datamanager.DataManager;
import matanku.kakurembo.enums.DataEnum;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class CoinDataManager extends DataManager {
    @Override
    public String configName() {
        return "coins";
    }

    @Override
    public Location loc() {
        return new Location(Bukkit.getWorld("world"), -6.0,-59,-6.0);
    }

    @Override
    public String name() {
        return "Coin";
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
        HideAndSeek.getInstance().getGame().getGamePlayer(UUID.fromString(playerName)).setCoin(i);
    }

    @Override
    public void setVariable(String playerName, String s) {

    }
}
