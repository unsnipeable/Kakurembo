package matanku.kakurembo.game;

import lombok.Getter;
import matanku.kakurembo.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import matanku.kakurembo.HideAndSeek;

import java.io.File;
import java.util.function.Consumer;

@Getter
public class GameMap {

    private World world;

    public GameMap() {
        File mapFolder = new File("plugins/" + HideAndSeek.INSTANCE.getDescription().getName() + "/maps/");
        if (!mapFolder.exists()) {
            mapFolder.mkdir();
        }
    }

    public void generateMap(Consumer<Boolean> callback) {
        Bukkit.unloadWorld("world_game", false);

        try {
            Util.deleteFile(new File("world_game"));
            Util.copyFolder(new File("plugins/" + HideAndSeek.INSTANCE.getDescription().getName() + "/maps/" + HideAndSeek.INSTANCE.getGame().getSettings().getMap()), new File(Bukkit.getWorldContainer() + File.separator + "world_game"));
            world = Util.loadWorld("world_game");
            callback.accept(true);
        } catch (Exception e) {
            e.printStackTrace();
            callback.accept(false);
        }
    }

    public void teleport(Player player) {
        player.teleport(world.getSpawnLocation());
    }

}
