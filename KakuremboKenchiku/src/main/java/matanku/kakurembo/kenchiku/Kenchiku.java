package matanku.kakurembo.kenchiku;

import lombok.Getter;
import lombok.Setter;
import matanku.kakurembo.api.KakuremboAPI;
import matanku.kakurembo.api.util.BasicConfigFile;
import matanku.kakurembo.kenchiku.command.*;
import matanku.kakurembo.kenchiku.listener.Listener;
import matanku.kakurembo.kenchiku.player.BuildPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public final class Kenchiku extends JavaPlugin {

    @Getter
    public static Kenchiku INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        getServer().getPluginManager().registerEvents(new Listener(), this);
        this.getCommand("lobby").setExecutor(new LobbyCommand());
        this.getCommand("l").setExecutor(new LobbyCommand());
        this.getCommand("server").setExecutor(new ServerCommand());
        this.getCommand("s").setExecutor(new ServerCommand());
        this.getCommand("world").setExecutor(new ServerCommand());
        this.getCommand("spec").setExecutor(new SpecCommand());
        this.getCommand("spectate").setExecutor(new SpecCommand());
        this.getCommand("exit").setExecutor(new ExitCommand());
        this.getCommand("sine").setExecutor(new SineCommand());
        this.getCommand("ping").setExecutor(new PingCommand());
        this.getCommand("tps").setExecutor(new TPSCommand());
    }

    public BuildPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }
    public BuildPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    private final Map<UUID, BuildPlayer> players = new HashMap<>();

    @Override
    public void onDisable() {
        KakuremboAPI.get().disable();
    }
}
