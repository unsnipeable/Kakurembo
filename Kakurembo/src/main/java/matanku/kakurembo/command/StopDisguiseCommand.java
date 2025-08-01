package matanku.kakurembo.command;

import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.api.util.Common;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import matanku.kakurembo.HideAndSeek;

public class StopDisguiseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player)commandSender;

        GamePlayer gamePlayer = HideAndSeek.INSTANCE.getGame().getGamePlayer(player);

        if (gamePlayer.getDisguises() == null) {
            Common.sendMessage(player, "<red>変身データがありません");
            return false;
        }

        if (gamePlayer.getDisguises().getDisguise() != null) {
            Common.sendMessage(player, "<red>あなたは変身していません");
            return false;
        }

        gamePlayer.getDisguises().getDisguise().stopDisguise();
        Common.sendMessage(player, "<green>成功しました");
        return true;
    }
}
