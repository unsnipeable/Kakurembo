package matanku.kakurembo;

import lombok.Getter;
import lombok.Setter;
import matanku.kakurembo.command.*;
import matanku.kakurembo.game.Game;
import matanku.kakurembo.game.GameListener;
import matanku.kakurembo.api.KakuremboAPI;
import matanku.kakurembo.api.util.BasicConfigFile;
import matanku.kakurembo.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public final class HideAndSeek extends JavaPlugin {

    @Getter
    public static HideAndSeek INSTANCE;

    private BasicConfigFile configFile;
    private BasicConfigFile mapFile;

    @Setter
    private boolean load;
    private Game game;

    public static GamePlayer getGamePlayerByPlayer(Player player) {
        for (GamePlayer gp : HideAndSeek.getINSTANCE().getGame().getPlayers().values()) {
            if (gp.getPlayer() == player) {
                return gp;
            }
        }
        return null;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        KakuremboAPI.get().enable(this);

        configFile = new BasicConfigFile(this, "config.yml");
        mapFile = new BasicConfigFile(this, "map.yml");

        game = new Game();

        getServer().getPluginManager().registerEvents(new GameListener(), this);

        this.getCommand("setbuilder").setExecutor(new setBuilderCommand());
        this.getCommand("settrackerenabled").setExecutor(new SetTrackerEnabledCommand());
        this.getCommand("countdown").setExecutor(new CountdownCommand());
        this.getCommand("roleselect").setExecutor(new RoleSelectCommand());
        this.getCommand("setlobby").setExecutor(new SetLobbyCommand());
        this.getCommand("settings").setExecutor(new SettingsCommand());
        this.getCommand("stopdisguise").setExecutor(new StopDisguiseCommand());
        this.getCommand("stoptask").setExecutor(new StopTaskCommand());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getPlayers() != null) {
                    for (GamePlayer gp : game.getPlayers().values()) {
                        if (gp.getPlayer().getWorld().getBlockAt(new Location(gp.getPlayer().getWorld(), 8, -60, 5)).getType() == Material.SEA_LANTERN) {
                            gp.onTickParkour();
                        }
                    }
                }
            }
        }.runTaskTimer(this,0L,1L);
    }

    @Override
    public void onDisable() {
        KakuremboAPI.get().disable();
    }
}
