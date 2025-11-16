package matanku.kakurembo.command.game;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.command.ServerCommand;
import matanku.kakurembo.util.Checker;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.util.enums.RankEnum;
import org.bukkit.entity.Player;

public class Countdown extends ServerCommand {
    public Countdown() {
        super("cooldown");
        this.setPerm(RankEnum.VIP);
    }

    @Override
    public void run(Player player, String[] args, String command) {
        if (HideAndSeek.Instance.getGame().getSettings().getMap() == null) {
            Common.sendMessage(player, "<red>マップがまだ選択されていません。 '/settings'コマンドを使用してマップを選択してください！");
            return;
        }

        if (Checker.isInteger(args[0])) {
            HideAndSeek.Instance.getGame().startCountdown(Integer.parseInt(args[0]));

            Common.sendMessage(player, "<green>カウントダウンが始まります！");
            return;
        }

        Common.sendMessage(player, "<red>使い方: /countdown <int>");
    }
}
