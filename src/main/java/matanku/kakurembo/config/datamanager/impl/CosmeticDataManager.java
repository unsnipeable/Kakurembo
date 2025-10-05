package matanku.kakurembo.config.datamanager.impl;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.config.Messages;
import matanku.kakurembo.config.datamanager.DataManager;
import matanku.kakurembo.enums.DataEnum;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CosmeticDataManager extends DataManager {
    @Override
    public String configName() {
        return "cosmetic";
    }

    @Override
    public Location loc() {
        return null;
    }

    @Override
    public String name() {
        return "Cosmetic";
    }

    @Override
    public String calc(double d) {
        return (int)d + "";
    }


    @Override
    public DataEnum.DataType type() {
        return DataEnum.DataType.STRING;
    }

    @Override
    public void setVariable(String playerName, Integer i) {
    }

    @Override
    public void setVariable(String playerName, String s) {
        List<Messages.Message> u = new ArrayList<>();
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i-1) == 'T') {
                u.add(Messages.getMessageFromI(i));
            }
        }
        HideAndSeek.getInstance().getGame().getGamePlayer(UUID.fromString(playerName)).setUnlockedKillMessage(u);
    }
}
