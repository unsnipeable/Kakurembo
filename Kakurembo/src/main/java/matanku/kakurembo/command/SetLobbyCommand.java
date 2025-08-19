package matanku.kakurembo.command;

import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.api.util.serialization.LocationSerialization;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import matanku.kakurembo.config.Config;
import matanku.kakurembo.HideAndSeek;

public class SetLobbyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player)commandSender;

        Location location = player.getLocation();

        Config.LOBBY_LOCATION = location;
        HideAndSeek.INSTANCE.getConfigFile().getConfiguration().set("lobby-location", LocationSerialization.serializeLocation(location));
        HideAndSeek.INSTANCE.getConfigFile().save();

        Common.sendMessage(player, "<green>ロビーを設定しました!");
        return true;
    }
}
