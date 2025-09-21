package matanku.kakurembo.kenchiku.util;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Util {
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
}
