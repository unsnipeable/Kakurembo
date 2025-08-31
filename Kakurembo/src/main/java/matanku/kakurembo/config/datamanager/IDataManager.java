package matanku.kakurembo.config.datamanager;

import matanku.kakurembo.enums.DataManagerType;
import matanku.kakurembo.enums.DataType;
import org.bukkit.Location;

public interface IDataManager {

    String configName();
    Location loc();
    String name();
    String calc(double d);
    DataType type();

}
