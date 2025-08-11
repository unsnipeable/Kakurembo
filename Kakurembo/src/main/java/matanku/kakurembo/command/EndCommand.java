package matanku.kakurembo.command;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.api.util.Checker;
import matanku.kakurembo.api.util.Common;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EndCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        try {
            HideAndSeek.getINSTANCE().getGame().end();
        } catch (AssertionError error) {
            Common.sendMessage((Player)commandSender, Common.text("<red>Error: " + error.getMessage()));
        }
        return false;
    }
}
