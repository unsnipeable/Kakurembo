package matanku.kakurembo.command;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.player.GamePlayer;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class muteCommand implements CommandExecutor {

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
            gp.setMuted(!gp.isMuted());
            Common.sendMessage(commandSender, "<red>" + gp.getPlayer().getName() + "を" + (gp.isMuted()?"":"アン") + "ミュートしましたwww");
        }
        return true;
    }
}
