package matanku.kakurembo.util;

import lombok.Getter;
import lombok.Setter;
import matanku.kakurembo.HideAndSeek;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class TaskTicker extends BukkitRunnable {

    public int tick;
    @Getter @Setter private boolean finishPreRun = false;

    public TaskTicker(int delay, int period, boolean async) {
        if (async) {
            this.runTaskTimerAsynchronously(HideAndSeek.getInstance(), delay, period);
        } else {
            this.runTaskTimer(HideAndSeek.getInstance(), delay, period);
        }
    }

    @Override
    public void run() {
        if (!finishPreRun) {
            tick = getStartTick();
            preRun();
            finishPreRun = true;
        }
        onRun();
        if (getTickType() == TickType.COUNT_UP) {
            countUp();
        } else if (getTickType() == TickType.COUNT_DOWN) {
            countDown();
        }
    }

    public abstract void onRun();

    public void preRun() {

    }

    public TickType getTickType() {
        return TickType.NONE;
    }

    public int getStartTick() {
        return 0;
    }

    public void countUp() {
        tick++;
    }

    public void countDown() {
        tick--;
    }

    public enum TickType {
        COUNT_UP,
        COUNT_DOWN,
        NONE
    }

}