package matanku.kakurembo.game.task.impl;

import matanku.kakurembo.game.task.GameTask;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.util.Util;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Sound;

public class HiderPhaseTask extends GameTask {
    public HiderPhaseTask(int seconds) {
        super(seconds);
    }

    @Override
    public void onRun() {
        if (tick == 0) {
            cancel();
            game.startSeekerPhase(game.getSettings().getTimes().getOrDefault("game_time",600));
        }
        if (Util.ANNOUNCE.contains(tick)) {
            Common.broadcastSound(Sound.UI_BUTTON_CLICK);
        }

        game.getBossBar().name("<yellow>シーカーは<aqua>" + Util.getTime(tick) + "<yellow>後にハイダーを探し始めます!").color(BossBar.Color.YELLOW).progress((float) tick / seconds);
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
