package matanku.kakurembo.config.datamanager;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import matanku.kakurembo.HideAndSeek;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class DataManager implements IDataManager {

    private Hologram hologram;
    private File dataFile;
    @Getter
    private FileConfiguration dataConfig;


    public void onEnable() {
        loadDataFile();
        if (Bukkit.getWorld("world") == null) return;
        createEmptyHologram(loc());

        new BukkitRunnable() {
            @Override
            public void run() {
                updateHologramLines();
            }
        }.runTaskTimer(HideAndSeek.getINSTANCE(), 0L, 20L); // 毎秒更新
    }

    public void loadDataFile() {
        dataFile = new File(HideAndSeek.getINSTANCE().getDataFolder(), configName() + ".yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(dataFile)) {
                writer.write("players: {}");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    private void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPlayerInfo(String playerName, int Integers) {
        int oldTicks = dataConfig.getInt("players." + playerName, Integer.MAX_VALUE);
        if (Integers < oldTicks) {
            dataConfig.set("players." + playerName, Integers);
            saveDataFile();
        }
    }

    public void addPlayerInfo2(String playerName, int Integers) {
        dataConfig.set("players." + playerName, Integers + dataConfig.getInt("players." + playerName, Integer.MAX_VALUE));
        saveDataFile();
    }

    private Map<String, Integer> getData() {
        Map<String, Integer> result = new HashMap<>();
        if (dataConfig.contains("players")) {
            for (String name : dataConfig.getConfigurationSection("players").getKeys(false)) {
                result.put(name, dataConfig.getInt("players." + name));
            }
        }
        return result;
    }


    public void createEmptyHologram(Location location) {
        List<String> lines = new ArrayList<>();
        lines.add("&e&l" + name() + " Leaderboard");
        for (int i = 1; i <= 10; i++) lines.add("&e" + i + ". &8Loading...");
        hologram = DHAPI.createHologram(configName() + "_LB", location, lines);
    }

    public void updateHologramLines() {
        Map<String, Integer> times = getData();
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(times.entrySet());
        sorted.sort(Map.Entry.comparingByValue());


        for (int i = 0; i < 10; i++) {
            String line;
            if (i < sorted.size()) {
                Map.Entry<String, Integer> entry = sorted.get(i);
                line = "&e" + (i + 1) + ". &6" + entry.getKey() + " &7- &e" + calc(entry.getValue()) + "s";
            } else {
                line = "&7" + (i + 1) + ". &8---";
            }
            DHAPI.setHologramLine(hologram, i + 1, line);
        }
    }
}
