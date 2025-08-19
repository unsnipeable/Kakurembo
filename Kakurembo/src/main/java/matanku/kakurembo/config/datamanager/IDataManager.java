package matanku.kakurembo.config.datamanager;

import org.bukkit.Location;

public interface IDataManager {

    String configName();
    Location loc();
    String name();
    String calc(double d);
}
