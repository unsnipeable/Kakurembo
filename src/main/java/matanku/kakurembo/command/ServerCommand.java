package matanku.kakurembo.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.util.enums.RankEnum;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
@RequiredArgsConstructor
public abstract class ServerCommand implements CommandExecutor {
    private RankEnum perm = RankEnum.NON;
    private String[] aliases = new String[]{};
    private final String name;

    public abstract void run(Player player, String[] args, String command);

    public GamePlayer gamePlayer(Player player) {
        try {
            return HideAndSeek.getInstance().getGame().getGamePlayer(player);
        } catch (Exception ignored) {return null;}
    }

    public boolean onCommand(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
        Player player = (Player) var1;
        if (gamePlayer(player).getRank().getWeight() < perm.getWeight()) {
            Common.sendMessage(player, "<red>あなたはこのコマンドを使用することができません!");
            return false;
        }
        run(player, var4, var3);
        return true;
    }
}