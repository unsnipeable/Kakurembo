package matanku.kakurembo.command;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.enums.ChatEnum;
import matanku.kakurembo.menu.RoleSelectAdminMenu;
import matanku.kakurembo.menu.RoleSelectMenu;
import matanku.kakurembo.player.GamePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player)commandSender;
        GamePlayer gamePlayer = HideAndSeek.getGamePlayerByPlayer(player);

        if (gamePlayer == null) return false;
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "party":
                case "p":
                    if (gamePlayer.getParty() == null) {
                        Common.sendMessage(gamePlayer.getPlayer(), "<green>あなたは今Partyに参加していません!");
                    } else {
                        gamePlayer.setFocusedChat(ChatEnum.PARTY);
                        Common.sendMessage(gamePlayer.getPlayer(), "<green>現在のチャットチャンネル: <gold>" + gamePlayer.getFocusedChat());
                    }
                    break;
                case "all":
                case "a":
                    gamePlayer.setFocusedChat(ChatEnum.ALL);
                    Common.sendMessage(gamePlayer.getPlayer(), "<green>現在のチャットチャンネル: <gold>" + gamePlayer.getFocusedChat());
                    break;
            }
            return true;
        } else {
            Common.sendMessage(gamePlayer.getPlayer(), "<green>現在のチャットチャンネル: <gold>" + gamePlayer.getFocusedChat());
        }

        return true;
    }
}
