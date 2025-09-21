package matanku.kakurembo.util;

import matanku.kakurembo.disguise.Disguise;
import matanku.kakurembo.disguise.impl.MiscDisguise;
import matanku.kakurembo.disguise.impl.MobDisguise;
import matanku.kakurembo.player.GamePlayer;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.enums.DisguiseTypes;
import matanku.kakurembo.game.disguise.DisguiseData;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Util {

    public static final List<Integer> ANNOUNCE = Arrays.asList(600, 500, 400, 300, 240, 180, 120, 60, 30, 20, 10, 5, 4, 3, 2, 1);

    public static String getSecFromTick(int ticks) {
        return String.format("%.1f秒", (double)ticks/20);
    }

    public static <T> T random(T... objects) {
        int i = new Random().nextInt(objects.length);
        return objects[i];
    }

    public static <T> T random(List<T> objects) {
        int i = new Random().nextInt(objects.size());
        return objects.get(i);
    }

    public static boolean deleteFile(File file) {
        if (file.exists()) {
            if(file.isDirectory()) {
                for(File subFile : Objects.requireNonNull(file.listFiles())) {
                    if(!deleteFile(subFile)) {
                        return false;
                    }
                }
            }

            return file.delete();
        }
        return false;
    }

    public static void copyFolder(File src, File dest) throws IOException {
        if(src.isDirectory()){
            if(!dest.exists()){
                dest.mkdir();
            }

            String files[] = src.list();

            for(String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);

                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            while((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        }
    }

    public static World loadWorld(String worldName) {
        World world = new WorldCreator(worldName).createWorld();

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setTime(0);
        world.setChunkForceLoaded(0, 0, true);
        world.loadChunk(0, 0);

        return world;
    }

    public static Disguise disguise(Player player) {
        GamePlayer gamePlayer = HideAndSeek.INSTANCE.getGame().getGamePlayer(player);
        DisguiseData disguiseData = gamePlayer.getDisguises();

        if (disguiseData.getType() == DisguiseTypes.BLOCK) {
            MiscDisguise disguise = new MiscDisguise(player, Material.valueOf(disguiseData.getData()));
            disguise.startDisguise();
            return disguise;
        } else if (disguiseData.getType() == DisguiseTypes.MOB) {
            MobDisguise disguise = new MobDisguise(player, disguiseData.getData());
            disguise.startDisguise();
            return disguise;
        }

        return null;
    }

    public static String getTime(int time) {
        if (time < 60) return time + "秒";
        int minutes = time / 60;
        int seconds = time % 60;
        return minutes + "分" + seconds + "秒";
    }
}
