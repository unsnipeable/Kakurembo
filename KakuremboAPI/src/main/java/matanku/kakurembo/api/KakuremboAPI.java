package matanku.kakurembo.api;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;

@Getter
public final class KakuremboAPI extends JavaPlugin {

    private static KakuremboAPI API;
    public static JavaPlugin PLUGIN;
    public static DecimalFormat DECIMAL = new DecimalFormat("0.##");


    public static KakuremboAPI get() {
        return API;
    }

    @Override
    public void onEnable() {
        API = this;
    }

    public KakuremboAPI enable(JavaPlugin plugin) {
        PLUGIN = plugin;

        return this;
    }

    public KakuremboAPI disable() {
        return this;
    }
}
