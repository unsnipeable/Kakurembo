package matanku.kakurembo.config.datamanager;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.Getter;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.enums.DataEnum;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
public abstract class DataManager {
    private Hologram hologram;
    private File dataFile;
    @Getter
    private FileConfiguration dataConfig;

    @Getter
    public boolean reverse = false;

    public void onEnable() {
        loadDataFile();
        if (type() == DataEnum.DataType.INTEGER) {
            if (Bukkit.getWorld("world") == null) return;
            if (loc() == null) return;
            createEmptyHologram(loc());

            new BukkitRunnable() {
                @Override
                public void run() {
                    updateHologramLines();
                }
            }.runTaskTimer(HideAndSeek.getInstance(), 0L, 20L); // 毎秒更新
        }
    }

    public void loadDataFile() {
        dataFile = new File(HideAndSeek.getInstance().getDataFolder() + "/data/", configName() + ".yml");
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

    // ====== データ登録 ======

    public void addPlayerInfoInteger(String uuid, int value, DataEnum.DataManagerType dmt) {
        if (type() != DataEnum.DataType.INTEGER) return; // 型チェック

        if (dmt == DataEnum.DataManagerType.SET) {
            dataConfig.set("players." + uuid, value);
            saveDataFile();
        } else if (dmt == DataEnum.DataManagerType.BETTER) {
            int oldValue = dataConfig.getInt("players." + uuid, Integer.MAX_VALUE);
            if (value == 0) return;
            if (value < oldValue) {
                dataConfig.set("players." + uuid, value);
                saveDataFile();
            }
        } else if (dmt == DataEnum.DataManagerType.ADD) {
            int oldValue = dataConfig.getInt("players." + uuid, 0);
            dataConfig.set("players." + uuid, oldValue + value);
            saveDataFile();
        }
    }

    public void addPlayerInfoString(String uuid, String value) {
        if (type() != DataEnum.DataType.STRING) return; // 型チェック

        dataConfig.set("players." + uuid, value);
        saveDataFile();
    }

    private Map<String, Integer> getIntData() {
        Map<String, Integer> result = new HashMap<>();
        if (dataConfig.contains("players")) {
            for (String name : dataConfig.getConfigurationSection("players").getKeys(false)) {
                Object value = dataConfig.get("players." + name);
                if (value instanceof Integer) {
                    result.put(name, (Integer) value);
                }
            }
        }
        return result;
    }


    public void createEmptyHologram(Location location) {
        List<String> lines = new ArrayList<>();
        lines.add("&e&l" + name() + " Leaderboard");
        for (int i = 1; i <= 10; i++) lines.add("&e" + i + ". &8Loading...");
        hologram = DHAPI.createHologram(configName() + "_LB", location.clone().add(new Vector(0,5,0)), lines);
    }

    public void updateHologramLines() {

        Map<String, Integer> times = getIntData();
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(times.entrySet());
        if (isReverse()) {
            sorted.sort(Map.Entry.comparingByValue());
        } else {
            sorted.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        }

        for (int i = 0; i < 10; i++) {
            String line;
            if (i < sorted.size()) {
                Map.Entry<String, Integer> entry = sorted.get(i);
                line = "&e" + (i + 1) + ". &6" +
                        Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey())).getName() +
                        " &7- &e" + calc(entry.getValue());
            } else {
                line = "&7" + (i + 1) + ". &8---";
            }
            DHAPI.setHologramLine(hologram, i + 1, line);
        }
    }

    // ====== サブクラスで実装必須 ======
    public abstract void setVariable(String playerName, Integer i);
    public abstract void setVariable(String playerName, String s);

    public abstract String configName();
    public abstract Location loc();
    public abstract String name();
    public abstract String calc(double d);
    public abstract DataEnum.DataType type();
}
