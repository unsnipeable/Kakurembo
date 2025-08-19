package matanku.kakurembo.kenchiku.command;

import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.kenchiku.Kenchiku;
import matanku.kakurembo.kenchiku.player.BuildPlayer;
import matanku.kakurembo.kenchiku.player.PlayerUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;

public class SineCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player)commandSender;
        player.kick(Common.text("omaega sine"), PlayerKickEvent.Cause.UNKNOWN);
        return false;
    }
}
