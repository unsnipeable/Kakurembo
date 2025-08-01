package matanku.kakurembo.command;

import matanku.kakurembo.api.util.Checker;
import matanku.kakurembo.api.util.Common;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import matanku.kakurembo.HideAndSeek;

public class CountdownCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player)commandSender;

        if (HideAndSeek.INSTANCE.getGame().getSettings().getMap() == null) {
            Common.sendMessage(player, "<red>カウントダウンを開始しようとしたときにエラーが発生しました: マップがまだ選択されていません! '/settings'コマンドを使用してマップを選択してください！");
            return false;
        }

        if (args.length == 1 && Checker.isInteger(args[0])) {
            HideAndSeek.INSTANCE.getGame().startCountdown(Integer.parseInt(args[0]));

            Common.sendMessage(player, "<green>カウントダウンが始まります！");
            return true;
        }

        Common.sendMessage(player, "<red>使い方: /countdown <int>");

        return false;
    }
}
