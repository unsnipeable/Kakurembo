package matanku.kakurembo.command.game;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.command.ServerCommand;
import matanku.kakurembo.game.task.impl.SeekerPhaseTask;
import matanku.kakurembo.util.Checker;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.util.enums.RankEnum;
import org.bukkit.entity.Player;

public class SetTracker extends ServerCommand {
    public SetTracker() {
        super("settracker");
        this.setPerm(RankEnum.ADMIN);
    }

    @Override
    public void run(Player player, String[] args, String command) {
        if (!HideAndSeek.Instance.getGame().isStarted()) {
            Common.sendMessage(player, "<red>ゲームが開始されていません。");
        }

        if (Checker.isBoolean(args[0])) {
            SeekerPhaseTask.setTracker(Boolean.parseBoolean(args[0]));

            Common.sendMessage(player, "<green>成功");
        } else {
            Common.sendMessage(player , "<red>入力された値" + args[0].toUpperCase() +"は真偽値ではありませんでした。");
        }
    }
}
