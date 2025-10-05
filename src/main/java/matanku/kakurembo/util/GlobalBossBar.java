package matanku.kakurembo.util;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GlobalBossBar {
    public BossBar bossbar;
    public Component name;
    public BossBar.Color color;
    public float progress;

    public GlobalBossBar(BossBar bossBar, String name) {
        this.bossbar = bossBar;
        this.name = Component.text(name);
    }

    public GlobalBossBar name(String s) {
        this.name = Common.text(s);
        this.bossbar.name(this.name);
        return this;
    }

    public GlobalBossBar color(BossBar.Color color) {
        this.color = color;
        this.bossbar.color(color);
        return this;
    }

    public GlobalBossBar progress(float i) {
        this.progress = i;
        this.bossbar.progress(i);
        return this;
    }

    public void destroy() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.hideBossBar(this.bossbar);
        }
    }

    public void show() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showBossBar(this.bossbar);
        }
    }
}
