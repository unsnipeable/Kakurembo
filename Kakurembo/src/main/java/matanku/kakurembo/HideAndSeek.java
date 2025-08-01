package matanku.kakurembo;

import lombok.Getter;
import matanku.kakurembo.command.*;
import matanku.kakurembo.game.Game;
import matanku.kakurembo.game.GameListener;
import matanku.kakurembo.api.KakuremboAPI;
import matanku.kakurembo.api.util.BasicConfigFile;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class HideAndSeek extends JavaPlugin {

    @Getter
    public static HideAndSeek INSTANCE;

    private BasicConfigFile configFile;
    private BasicConfigFile mapFile;

    private Game game;


    @Override
    public void onEnable() {
        INSTANCE = this;

        KakuremboAPI.get().enable(this);

        configFile = new BasicConfigFile(this, "config.yml");
        mapFile = new BasicConfigFile(this, "map.yml");

        game = new Game();

        getServer().getPluginManager().registerEvents(new GameListener(), this);

        this.getCommand("settrackerenabled").setExecutor(new SetTrackerEnabledCommand());
        this.getCommand("countdown").setExecutor(new CountdownCommand());
        this.getCommand("roleselect").setExecutor(new RoleSelectCommand());
        this.getCommand("setlobby").setExecutor(new SetLobbyCommand());
        this.getCommand("settings").setExecutor(new SettingsCommand());
        this.getCommand("stopdisguise").setExecutor(new StopDisguiseCommand());
        this.getCommand("stoptask").setExecutor(new StopTaskCommand());
    }

    @Override
    public void onDisable() {
        KakuremboAPI.get().disable();
    }
}
