package matanku.kakurembo.command;

import matanku.kakurembo.api.util.Common;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.menu.DisguiseSelectMenu;

public class DisguiseSelectCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player)commandSender;


        if (HideAndSeek.INSTANCE.getGame().getSettings().getMap() == null) {
            Common.sendMessage(player, "<red>ホストは変装を選択する前にマップを選択する必要があります!");
            return false;
        }

        new DisguiseSelectMenu().openMenu(player);
        return true;
    }
}
