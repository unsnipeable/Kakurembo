package matanku.kakurembo.config.datamanager;

import lombok.Getter;
import matanku.kakurembo.config.datamanager.impl.CoinDataManager;
import matanku.kakurembo.config.datamanager.impl.CosmeticDataManager;
import matanku.kakurembo.config.datamanager.impl.ParkourDataManager;
import matanku.kakurembo.config.datamanager.impl.TrollDataManager;
import matanku.kakurembo.enums.DataManagerType;

import java.util.ArrayList;
import java.util.Objects;

@Getter
public class Manager {
    @Getter
    private static ArrayList<DataManager> managers;

    private static void init() {
        managers = new ArrayList<>();
        managers.add(new ParkourDataManager());
        managers.add(new TrollDataManager());
        managers.add(new CoinDataManager());
        managers.add(new CosmeticDataManager());
    }

    public static void register() {
        register(false);
    }
    public static void register(boolean b) {
        if (managers == null) init();
        for (DataManager dm : managers) {
            if (b) {
                dm.loadDataFile();
            } else {
                dm.onEnable();
            }
        }
    }

    public static DataManager getDataManager(String dms) {
        for (DataManager dm : Manager.getManagers()) {
            if (dms.equalsIgnoreCase(dm.getClass().getSimpleName())) {
                return dm;
            }
        }
        return null;
    }
}
