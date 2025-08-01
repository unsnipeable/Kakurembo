package matanku.kakurembo.command;

import matanku.kakurembo.api.util.Common;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import matanku.kakurembo.HideAndSeek;

public class StopTaskCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player)commandSender;


        HideAndSeek.INSTANCE.getGame().getCurrentTask().cancel();
        HideAndSeek.INSTANCE.getGame().setCurrentTask(null);

        Common.sendMessage(player, "<green>実行中のBukkitTaskを停止しました");
        return true;
    }
}
