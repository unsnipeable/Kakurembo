package matanku.kakurembo.disguise;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

@RequiredArgsConstructor
public abstract class Disguise {

    public final Player player;
    public boolean disguised = false;
    public BukkitTask task;

    public abstract void startDisguise();

    public abstract void stopDisguise();

}
