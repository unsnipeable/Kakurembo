package matanku.kakurembo;

import lombok.Getter;
import lombok.Setter;
import matanku.kakurembo.command.*;
import matanku.kakurembo.config.datamanager.Manager;
import matanku.kakurembo.enums.GameState;
import matanku.kakurembo.enums.Items;
import matanku.kakurembo.game.Game;
import matanku.kakurembo.game.GameListener;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.util.BasicConfigFile;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.util.PlaceHolderAPIUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

@Getter
public final class HideAndSeek extends JavaPlugin {

    @Getter
    public static HideAndSeek Instance;

    private BasicConfigFile configFile;
    private BasicConfigFile mapFile;

    @Setter
    private boolean load;
    private Game game;

    @NotNull
    public static GamePlayer getGamePlayerByPlayer(Player player) {
        return getInstance().game.getGamePlayer(player);
    }
    public static DecimalFormat DECIMAL = new DecimalFormat("0.##");


    @Override
    public void onEnable() {
        Instance = this;


        configFile = new BasicConfigFile(this, "config.yml");
        mapFile = new BasicConfigFile(this, "map.yml");

        game = new Game();

        getServer().getPluginManager().registerEvents(new GameListener(), this);

        this.getCommand("hideandseek").setExecutor(new HideAndSeekCommand());
        this.getCommand("settings").setExecutor(new SettingsCommand());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getPlayers() != null) {
                    for (GamePlayer gp : game.getPlayers().values()) {
                        if (!getGame().isStarted() && getGame().getState() == GameState.WAITING) {
                            gp.getPlayer().sendActionBar(Common.text("<red>シーカーになる確率: " + (int)Math.round((double)getGame().getSettings().getMaxSeekers()/getGame().getPlayers().size()*100) + "% <aqua>ハイダーになる確率: " + (100-(int)Math.round((double)getGame().getSettings().getMaxSeekers()/getGame().getPlayers().size()*100)) + "%"));
                        }

                        if (!game.isStarted()) {
                            if (!gp.isParkour()) {
                                gp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED,Integer.MAX_VALUE,1, true, true));
                            } else gp.getPlayer().removePotionEffect(PotionEffectType.SPEED);

                            gp.onTickParkour();
                            gp.getPlayer().getInventory().setItem(1, Items.COSMETIC.getItem());
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
}
