package matanku.kakurembo.command;

import matanku.kakurembo.menu.RoleSelectAdminMenu;
import matanku.kakurembo.menu.RoleSelectMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RoleSelectCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player)commandSender;

        if (args.length == 1 && args[0].equalsIgnoreCase("admin") && player.hasPermission("op")) {
            new RoleSelectAdminMenu().openMenu(player);
            return true;
        }

        new RoleSelectMenu().openMenu(player);
        return true;
    }
}
