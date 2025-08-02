package matanku.kakurembo.util;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import matanku.kakurembo.HideAndSeek;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkourUtil {

    private static File dataFile;
    private static FileConfiguration dataConfig;
    private static Hologram hologram;

    public static void onEnable() {
        ParkourUtil.loadDataFile();
        if (Bukkit.getWorld("world") == null) return;
        Location loc = Bukkit.getWorld("world").getBlockAt(15,-56 ,11).getLocation();
        ParkourUtil.createEmptyHologram(loc);

        new BukkitRunnable() {
            @Override
            public void run() {
                ParkourUtil.updateHologramLines();
            }
        }.runTaskTimer(HideAndSeek.getINSTANCE(), 0L, 20L); // 毎秒更新
    }

    public static void loadDataFile() {
        dataFile = new File(HideAndSeek.getINSTANCE().getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            HideAndSeek.getINSTANCE().saveResource("data.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    private static void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addPlayerInfo(String playerName, int ticks) {
        int oldTicks = dataConfig.getInt("players." + playerName, Integer.MAX_VALUE);
        if (ticks < oldTicks) {
            dataConfig.set("players." + playerName, ticks);
            saveDataFile();
        }
    }

    private static Map<String, Integer> getParkourTimes() {
        Map<String, Integer> result = new HashMap<>();
        if (dataConfig.contains("players")) {
            for (String name : dataConfig.getConfigurationSection("players").getKeys(false)) {
                result.put(name, dataConfig.getInt("players." + name));
            }
        }
        return result;
    }

    public static void createEmptyHologram(Location location) {
        List<String> lines = new ArrayList<>();
        lines.add("&e&lLeaderboard");
        for (int i = 1; i <= 10; i++) lines.add("&e" + i + ". &8Loading...");
        hologram = DHAPI.createHologram("parkour_leaderboard", location, lines);
    }

    public static void updateHologramLines() {
        Map<String, Integer> times = getParkourTimes();
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(times.entrySet());
        sorted.sort(Map.Entry.comparingByValue());

        DecimalFormat df = new DecimalFormat("#0.00");

        for (int i = 0; i < 10; i++) {
            String line;
            if (i < sorted.size()) {
                Map.Entry<String, Integer> entry = sorted.get(i);
                double seconds = entry.getValue() / 20.0;
                line = "&e" + (i + 1) + ". &6" + entry.getKey() + " &7- &e" + df.format(seconds) + "s";
            } else {
                line = "&7" + (i + 1) + ". &8---";
            }
            DHAPI.setHologramLine(hologram, i + 1, line); // 0はタイトル
        }
    }
}
