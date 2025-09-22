package matanku.kakurembo.game.task.impl;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.game.task.GameTask;
import matanku.kakurembo.util.Util;
import matanku.kakurembo.api.util.Common;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Sound;

public class CountdownPhaseTask extends GameTask {
    public CountdownPhaseTask(int seconds) {
        super(seconds);
    }


    @Override
    public void onRun() {
        if (tick == 0) {
            if (HideAndSeek.getInstance().isLoad()) return;
            HideAndSeek.getInstance().setLoad(true);
            cancel();
            game.generateWorld();
            return;
        }
        if (Util.ANNOUNCE.contains(tick)) {
            Common.broadcastSound(Sound.UI_BUTTON_CLICK);
        }

        if (tick < 0) {
            cancel();
            game.generateWorld();
        }
        if (game.getBossBar() == null) return;
        game.getBossBar().name("<yellow>この試合は<aqua>" + Util.getTime(tick) + "<yellow>後に開始します").color(BossBar.Color.YELLOW).progress(Math.max((float) tick / seconds,0f));
        game.getBossBar().show();
    }

    @Override
    public TickType getTickType() {
        return TickType.COUNT_DOWN;
    }

    @Override
    public int getStartTick() {
        return seconds;
    }
}
