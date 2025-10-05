package matanku.kakurembo.game.task.impl;

import matanku.kakurembo.game.task.GameTask;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.util.Util;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Sound;

public class InstructionPhaseTask extends GameTask {

    public InstructionPhaseTask(int seconds) {
        super(seconds);
    }

    @Override
    public void onRun() {
        if (tick == 0) {
            cancel();
            game.startHiderPhase(game.getSettings().getTimes().getOrDefault("hider_time", 180));
        }
        if (Util.ANNOUNCE.contains(tick)) {
            Common.broadcastSound(Sound.UI_BUTTON_CLICK);
        }

        game.getBossBar().name("<yellow>ハイダーは<aqua>" + Util.getTime(tick) + "<yellow>後に隠れ場所を探しはじめます!").color(BossBar.Color.YELLOW).progress((float) tick / seconds);
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
