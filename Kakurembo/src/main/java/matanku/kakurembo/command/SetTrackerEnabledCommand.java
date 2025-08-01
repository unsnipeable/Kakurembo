package matanku.kakurembo.command;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.api.util.Checker;
import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.game.task.impl.SeekerPhaseTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetTrackerEnabledCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player)commandSender;

        if (!HideAndSeek.INSTANCE.getGame().isStarted()) {
            Common.sendMessage(player, "<red>ゲームが開始されていません。");
            return false;
        }

        if (args.length == 1 && Checker.isBoolean(args[0])) {
            SeekerPhaseTask.setTracker(Boolean.parseBoolean(args[0]));

            Common.sendMessage(player, "<green>WOOO");
            return true;
        }

        return false;
    }
}
