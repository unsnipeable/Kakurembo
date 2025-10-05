package matanku.kakurembo.config.datamanager;

import matanku.kakurembo.enums.DataEnum;
import org.bukkit.Location;

public interface IDataManager {

    String configName();
    Location loc();
    String name();
    String calc(double d);
    DataEnum.DataType type();

}
