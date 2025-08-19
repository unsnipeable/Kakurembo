package matanku.kakurembo;

import lombok.Getter;
import lombok.Setter;
import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.command.*;
import matanku.kakurembo.config.datamanager.Manager;
import matanku.kakurembo.enums.GameState;
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
        this.getCommand("end").setExecutor(new EndCommand());
        this.getCommand("mute").setExecutor(new muteCommand());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getPlayers() != null) {
                    for (GamePlayer gp : game.getPlayers().values()) {
                        if (gp.getPlayer().getWorld().getBlockAt(new Location(gp.getPlayer().getWorld(), 8, -60, 5)).getType() == Material.SEA_LANTERN) {
                            gp.onTickParkour();
                        }
                        if (!getGame().isStarted() && getGame().getState() == GameState.WAITING) {
                            gp.getPlayer().sendActionBar(Common.text("<red>シーカーになる確率: " + (int) ((getGame().getSettings().getMaxSeekers() / getGame().getPlayers().values().size()) * 100) + "% <aqua>ハイダーになる確率: " + (int) (100-((getGame().getSettings().getMaxSeekers() / getGame().getPlayers().values().size()) * 100)) + "%"));
                        }
                    }
                }
            }
        }.runTaskTimer(this,0L,1L);


        new BukkitRunnable() {
            @Override
            public void run() {
            }
        }.runTaskTimer(HideAndSeek.getINSTANCE(),0L,1L);

        Manager.register();
    }

    @Override
    public void onDisable() {
        KakuremboAPI.get().disable();
    }
}
