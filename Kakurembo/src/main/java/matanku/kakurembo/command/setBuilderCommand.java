package matanku.kakurembo.command;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.menu.RoleSelectAdminMenu;
import matanku.kakurembo.menu.RoleSelectMenu;
import matanku.kakurembo.player.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class setBuilderCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            GamePlayer gp = null;
            for (GamePlayer ga : HideAndSeek.INSTANCE.getGame().getPlayers().values()) {
                if (ga.getPlayer().getName().contains(args[0])) {
                    gp = ga;
                }
            }
            if (gp == null) return false;
            gp.setEnableBuild(!gp.isEnableBuild());
            if (gp.isEnableBuild()) {
                gp.getPlayer().setGameMode(GameMode.CREATIVE);
                Common.sendMessage(gp.getPlayer(), "<green>ビルド権限が渡りました!");
            } else {
                gp.getPlayer().setGameMode(GameMode.SURVIVAL);
                Common.sendMessage(gp.getPlayer(), "<red>ビルド権限が剥奪されました!");
            }
        }
        return true;
    }
}
