package matanku.kakurembo.kenchiku.command;

import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.kenchiku.Kenchiku;
import matanku.kakurembo.kenchiku.player.BuildPlayer;
import matanku.kakurembo.kenchiku.player.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpecCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player)commandSender;
        BuildPlayer buildPlayer = Kenchiku.getINSTANCE().getPlayer(player);

        if (args.length == 1) {
            if (buildPlayer.beforeSpectate == null) {
                buildPlayer.beforeSpectate = player.getLocation();
                boolean a = false;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().equalsIgnoreCase(args[0])) {
                        player.teleport(p);
                        a = true;
                        break;
                    }
                }
                if (a) {
                    buildPlayer.setBeforeSpectateArm(player.getInventory().getArmorContents());
                    buildPlayer.setBeforeSpectateInv(player.getInventory().getContents());
                    PlayerUtil.spectator(player);
                    Common.sendMessage(player,"<red>あなたは今スペクテイターです。/exitを使用してスペクテイターから戻ります。");
                } else {
                    Common.sendMessage(player,"<red>" + args[0] + " という名前のプレイヤーは見当たりませんでした。");
                }
            } else {
                Common.sendMessage(player,"<red>あなたは既にスペクテイターです。/exitを使用してスペクテイターから戻ります");
            }
        }
        return false;
    }
}
