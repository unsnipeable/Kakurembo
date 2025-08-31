package matanku.kakurembo.config.datamanager.impl;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.config.datamanager.DataManager;
import matanku.kakurembo.enums.DataManagerType;
import matanku.kakurembo.enums.DataType;
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
        return Bukkit.getWorld("world").getBlockAt(15,-56 ,21).getLocation();
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
    public DataType type() {
        return DataType.INTEGER;
    }

    @Override
    public void setVariable(String playerName, Integer i) {
        HideAndSeek.getINSTANCE().getGame().getGamePlayer(UUID.fromString(playerName)).setCoin(i);
    }

    @Override
    public void setVariable(String playerName, String s) {

    }
}
