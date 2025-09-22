package matanku.kakurembo.config.datamanager;

import lombok.Getter;
import matanku.kakurembo.config.datamanager.impl.CoinDataManager;
import matanku.kakurembo.config.datamanager.impl.CosmeticDataManager;
import matanku.kakurembo.config.datamanager.impl.ParkourDataManager;
import matanku.kakurembo.config.datamanager.impl.TrollDataManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Manager {
    @Getter
    private static Map<Integer, DataManager> managers;

    private static void init() {
        managers = new HashMap<>();
        managers.put(0,new ParkourDataManager());
        managers.put(1,new TrollDataManager());
        managers.put(2,new CoinDataManager());
        managers.put(3,new CosmeticDataManager());
    }

    public static void register() {
        register(false);
    }
    public static void register(boolean withoutCreateHologram) {
        if (managers == null) init();
        for (DataManager dm : managers.values()) {
            if (withoutCreateHologram) {
                dm.loadDataFile();
            } else {
                dm.onEnable();
            }
        }
    }

    public static DataManager getDataManager(String dms) {
        for (DataManager dm : Manager.getManagers().values()) {
            if (dms.equalsIgnoreCase(dm.getClass().getSimpleName())) {
                return dm;
            }
        }
        return null;
    }
}
