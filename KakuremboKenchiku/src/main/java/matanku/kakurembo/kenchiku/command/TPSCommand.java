package matanku.kakurembo.kenchiku.command;

import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.kenchiku.Kenchiku;
import matanku.kakurembo.kenchiku.player.BuildPlayer;
import matanku.kakurembo.kenchiku.player.PlayerUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TPSCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player)commandSender;
        Common.sendMessage(player, "<yellow>[Kakurembo] <white>Current TPS:", "<dark_gray>| <gold>" + Kenchiku.getINSTANCE().getServer().getTPS()[0] + "<gray>(1min)", "<dark_gray>| <gold>" + Kenchiku.getINSTANCE().getServer().getTPS()[1] + "<gray>(5min)", "<dark_gray>| <gold>" + Kenchiku.getINSTANCE().getServer().getTPS()[2] + "<gray>(15m)");
        return false;
    }
}
