package matanku.kakurembo.config.datamanager;

import lombok.Getter;
import lombok.NonNull;
import matanku.kakurembo.config.datamanager.impl.*;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Manager {
    @Getter
    private static Map<Class<? extends DataManager>, DataManager> managers;

    private static void init() {
        managers = new HashMap<>();
        managers.put(ParkourDataManager.class,new ParkourDataManager());
        managers.put(TrollDataManager.class,new TrollDataManager());
        managers.put(CoinDataManager.class,new CoinDataManager());
        managers.put(CosmeticDataManager.class,new CosmeticDataManager());
        managers.put(StarProgressDataManager.class,new StarProgressDataManager());
        managers.put(StarDataManager.class,new StarDataManager());
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

    @SuppressWarnings("unchecked")
    @NotNull
    public static <T extends DataManager> T getDataManager(Class<T> dms) {
        for (DataManager dm : Manager.getManagers().values()) {
            if (dm.getClass() == dms) {
                return (T) dm;
            }
        }
        throw new NullPointerException("Excepted non-existed manager");
    }

}
