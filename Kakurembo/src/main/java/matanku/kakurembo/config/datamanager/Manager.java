package matanku.kakurembo.config.datamanager;

import lombok.Getter;
import matanku.kakurembo.config.datamanager.impl.ParkourDataManager;
import matanku.kakurembo.config.datamanager.impl.TrollDataManager;

import java.util.ArrayList;

@Getter
public class Manager {
    @Getter
    private static ArrayList<DataManager> managers;

    private static void init() {
        managers = new ArrayList<>();
        managers.add(new ParkourDataManager());
        managers.add(new TrollDataManager());
    }

    public static void register() {
        if (managers == null) init();
        for (DataManager dm : managers) {
            dm.onEnable();
        }
    }
}
