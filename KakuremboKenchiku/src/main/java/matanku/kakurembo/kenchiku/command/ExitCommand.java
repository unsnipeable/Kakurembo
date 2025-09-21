package matanku.kakurembo.kenchiku.command;

import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.kenchiku.Kenchiku;
import matanku.kakurembo.kenchiku.player.BuildPlayer;
import matanku.kakurembo.kenchiku.player.PlayerUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ExitCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player)commandSender;
        BuildPlayer buildPlayer = Kenchiku.getINSTANCE().getPlayer(player);

        if (buildPlayer.beforeSpectate != null) {
            player.teleport(buildPlayer.beforeSpectate);
            buildPlayer.beforeSpectate = null;
            PlayerUtil.reset(player);
            player.getInventory().setArmorContents(buildPlayer.beforeSpectateArm);
            player.getInventory().setContents(buildPlayer.beforeSpectateInv);
        } else {
            Common.sendMessage(player,"<red>あなたはスペクテイターではありません。");
        }

        return false;
    }
}
