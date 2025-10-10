package matanku.kakurembo.util;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.disguise.Disguise;
import matanku.kakurembo.disguise.impl.MiscDisguise;
import matanku.kakurembo.game.disguise.DisguiseData;
import matanku.kakurembo.player.GamePlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

public class Util {

    public static List<Integer> generateNumbers(int amount, int start, int end) {
        List<Integer> list = new ArrayList<>();
        for(int i = start; i <= end; i++){
            list.add(i);
        }
        Collections.shuffle(list);
        return list.subList(0, amount);
    }

    public static void hideNameTags(Player player) {
        String teamName = "API";
        //gets the players scoreboard and creates a new one if it does not exist
        Scoreboard scoreboard = player.getScoreboard();
        //loops through all online players
        for (Player all : Bukkit.getOnlinePlayers()) {
            //gets all the players scoreboard and creates a new one if it does not exist
            Scoreboard s = all.getScoreboard();

            //creates a new team with the players name if it does not exist.
            Team team = s.getTeam(teamName);
            if (team == null) team = s.registerNewTeam(teamName);

            //this is where the name changing comes in. changing the prefix of the team shows up above the players nametag
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            if (!team.hasEntry(player.getName())) {
                team.addEntry(player.getName());
            }
            all.setScoreboard(s); //sets every players' scoreboard, adding "player"'s name to the tablist and above their character

            if (all != player) continue; //Checks if all is not equal to the player so player can receive team prefixes of other players in their own tablist

            Team t = scoreboard.getTeam(teamName);
            if (t == null) t = scoreboard.registerNewTeam(teamName);

            t.addPlayer(all);
        }
    }


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

            String[] files = src.list();

            for(String file : Objects.requireNonNull(files)) {
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
        World world = Objects.requireNonNull(new WorldCreator(worldName).createWorld());

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setTime(0);
        world.setChunkForceLoaded(0, 0, true);
        world.loadChunk(0, 0);

        return world;
    }

    public static Disguise disguise(Player player) {
        GamePlayer gamePlayer = HideAndSeek.Instance.getGame().getGamePlayer(player);
        DisguiseData disguiseData = gamePlayer.getDisguises();

            MiscDisguise disguise = new MiscDisguise(player, Material.valueOf(disguiseData.getData()));
            disguise.startDisguise();
            return disguise;
    }

    public static String getTime(int time) {
        if (time < 60) return time + "秒";
        int minutes = time / 60;
        int seconds = time % 60;
        return minutes + "分" + seconds + "秒";
    }
}
