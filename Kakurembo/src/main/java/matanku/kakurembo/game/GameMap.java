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
    private String name;

    public GameMap() {
        File mapFolder = new File("plugins/" + HideAndSeek.INSTANCE.getDescription().getName() + "/maps/");
        if (!mapFolder.exists()) {
            mapFolder.mkdir();
        }
    }

    public void generateMap(String worldName, Consumer<Boolean> callback) {
        removeMap(worldName);

        try {
            name = HideAndSeek.INSTANCE.getGame().getSettings().getMap();
            Util.copyFolder(new File("plugins/" + HideAndSeek.INSTANCE.getDescription().getName() + "/maps/" + getName()), new File(Bukkit.getWorldContainer() + File.separator + worldName));
            world = Util.loadWorld(worldName);
            callback.accept(true);
        } catch (Exception e) {
            e.printStackTrace();
            callback.accept(false);
        }
    }

    public static void removeMap(String worldName) {
        Bukkit.unloadWorld(worldName, false);
        Util.deleteFile(new File(worldName));
    }

    public void teleport(Player player) {
        player.teleport(world.getSpawnLocation());
    }

}
