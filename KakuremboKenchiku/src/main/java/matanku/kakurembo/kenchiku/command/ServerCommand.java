package matanku.kakurembo.kenchiku.command;

import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.kenchiku.Kenchiku;
import matanku.kakurembo.kenchiku.player.BuildPlayer;
import matanku.kakurembo.kenchiku.player.PlayerUtil;
import matanku.kakurembo.kenchiku.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class ServerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player)commandSender;
        BuildPlayer buildPlayer = Kenchiku.getINSTANCE().getPlayer(player);

        if (args.length == 1) {
            if (buildPlayer.beforeSpectate != null) {
                buildPlayer.beforeSpectate = null;
                PlayerUtil.reset(player);
                player.getInventory().setArmorContents(buildPlayer.beforeSpectateArm);
                player.getInventory().setContents(buildPlayer.beforeSpectateInv);
            }
            generateMap(args[0], player, (bool)->{
                if (bool) {
                    player.teleport(Objects.requireNonNull(Bukkit.getWorld(args[0])).getSpawnLocation());
                } else {
                    Common.sendMessage(player,"<red>マップ生成中にエラーが発生しました。");
                }
            });
        } else {
            Common.sendMessage(player, "<yellow>マップ:");
            for (File f : getWorlds()) {
                Common.sendMessage(player,"<gray>| <gold>" + f.getName());
            }
            Common.sendMessage(player, "<gray>/server <gold>ワールド名");
        }
        return false;
    }

    public static List<File> getWorlds() {
        File serverDir = new File(".");
        File[] dirs = serverDir.listFiles(File::isDirectory);
        if (dirs == null) return Collections.emptyList();
        List<File> worldFolders = new ArrayList<>();
        for (File dir : dirs) {
            if (Set.of("world", "world_nether", "world_the_end").contains(dir.getName())) continue;
            File levelDat = new File(dir, "level.dat");
            if (levelDat.exists()) {
                worldFolders.add(dir);
            }
        }
        return worldFolders;
    }

    public void generateMap(String worldName, Player player, Consumer<Boolean> callback) {
        try {
            World w = Util.loadWorld(worldName);
            player.teleport(w.getSpawnLocation());
            callback.accept(true);
        } catch (Exception e) {
            e.printStackTrace();
            callback.accept(false);
        }
    }
}
