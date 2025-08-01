package matanku.kakurembo.api.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import matanku.kakurembo.api.KakuremboAPI;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ThreadFactory;

public class Tasks {

    public static ThreadFactory newThreadFactory(String name) {
        return new ThreadFactoryBuilder().setNameFormat(name).build();
    }

    public static void run(Runnable runnable, boolean async) {
        if(async) {
            KakuremboAPI.PLUGIN.getServer().getScheduler().runTaskAsynchronously(KakuremboAPI.PLUGIN, runnable);
        } else {
            runnable.run();
        }
    }

    public static BukkitTask run(Runnable runnable) {
        return KakuremboAPI.PLUGIN.getServer().getScheduler().runTask(KakuremboAPI.PLUGIN, runnable);
    }

    public static BukkitTask runAsync(Runnable runnable) {
        return KakuremboAPI.PLUGIN.getServer().getScheduler().runTaskAsynchronously(KakuremboAPI.PLUGIN, runnable);
    }

    public static BukkitTask runLater(Runnable runnable, long delay) {
        return KakuremboAPI.PLUGIN.getServer().getScheduler().runTaskLater(KakuremboAPI.PLUGIN, runnable, delay);
    }

    public static BukkitTask runAsyncLater(Runnable runnable, long delay) {
        return KakuremboAPI.PLUGIN.getServer().getScheduler().runTaskLaterAsynchronously(KakuremboAPI.PLUGIN, runnable, delay);
    }

    public static BukkitTask runTimer(Runnable runnable, long delay, long interval) {
        return KakuremboAPI.PLUGIN.getServer().getScheduler().runTaskTimer(KakuremboAPI.PLUGIN, runnable, delay, interval);
    }

    public static BukkitTask runAsyncTimer(Runnable runnable, long delay, long interval) {
        return KakuremboAPI.PLUGIN.getServer().getScheduler().runTaskTimerAsynchronously(KakuremboAPI.PLUGIN, runnable, delay, interval);
    }

    public static BukkitScheduler getScheduler() {
        return KakuremboAPI.PLUGIN.getServer().getScheduler();
    }
}
