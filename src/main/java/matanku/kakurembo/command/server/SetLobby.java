package matanku.kakurembo.command.server;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.command.ServerCommand;
import matanku.kakurembo.config.Config;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.util.enums.RankEnum;
import matanku.kakurembo.util.serialization.LocationSerialization;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetLobby extends ServerCommand {
    public SetLobby() {
        super("setlobby");
        this.setPerm(RankEnum.ADMIN);
    }

    @Override
    public void run(Player player, String[] args, String command) {
        Location location = player.getLocation();

        Config.LOBBY_LOCATION = location;
        HideAndSeek.Instance.getConfigFile().getConfiguration().set("lobby-location", LocationSerialization.serializeLocation(location));
        HideAndSeek.Instance.getConfigFile().save();

        Common.sendMessage(player, "<green>ロビーを設定しました!");
    }
}
