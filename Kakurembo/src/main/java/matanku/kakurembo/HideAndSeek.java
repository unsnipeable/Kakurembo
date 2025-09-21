package matanku.kakurembo;

import lombok.Getter;
import lombok.Setter;
import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.api.util.ItemBuilder;
import matanku.kakurembo.command.*;
import matanku.kakurembo.config.datamanager.Manager;
import matanku.kakurembo.enums.GameState;
import matanku.kakurembo.game.Game;
import matanku.kakurembo.game.GameListener;
import matanku.kakurembo.api.KakuremboAPI;
import matanku.kakurembo.api.util.BasicConfigFile;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.util.PlaceHolderAPIUtil;
import org.bukkit.Bukkit;
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

        this.getCommand("hideandseek").setExecutor(new HideAndSeekCommand());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getPlayers() != null) {
                    for (GamePlayer gp : game.getPlayers().values()) {
                        if (!game.isStarted()) {
                            gp.onTickParkour();
                        }
                        if (!getGame().isStarted() && getGame().getState() == GameState.WAITING) {
                            gp.getPlayer().sendActionBar(Common.text("<red>シーカーになる確率: " + (int)Math.round((double)getGame().getSettings().getMaxSeekers()/getGame().getPlayers().size()*100) + "% <aqua>ハイダーになる確率: " + (100-(int)Math.round((double)getGame().getSettings().getMaxSeekers()/getGame().getPlayers().size()*100)) + "%"));
                        }

                        if (!game.isStarted()) {
                            gp.getPlayer().getInventory().setItem(1,Items.COSMETIC.getItem());
                        }
                    }
                }
            }
        }.runTaskTimer(this,0L,1L);

        Manager.register();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { //
            new PlaceHolderAPIUtil(this).register(); //
        }
    }

    @Override
    public void onDisable() {
        KakuremboAPI.get().disable();
    }
}
