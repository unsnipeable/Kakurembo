package matanku.kakurembo.util;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.enums.GameRole;
import matanku.kakurembo.player.GamePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.stream.Collectors;

public class PlaceHolderAPIUtil extends PlaceholderExpansion {

    private final HideAndSeek plugin;

    public PlaceHolderAPIUtil(HideAndSeek plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors()); //
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "hideandseek";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        DecimalFormat df = new DecimalFormat("0.00");
        if (params.equalsIgnoreCase("isparkour")) {
            return plugin.getGame().getGamePlayer(player).isParkour() + "";
        }
        if (params.equalsIgnoreCase("started")) {
            return plugin.getGame().isStarted() + "";
        }
        if (params.equalsIgnoreCase("players")) {
            return plugin.getGame().getPlayers().size() + "";
        }
        if (params.equalsIgnoreCase("map")) {
            return plugin.getGame().getMap().getName();
        }
        if (params.equalsIgnoreCase("hider")) {
            return (int)plugin.getGame().getPlayers().values().stream().filter((gamePlayer -> gamePlayer.getRole() == GameRole.HIDER)).count() + "";
        }
        if (params.equalsIgnoreCase("stunc")) {
            return (Math.max(plugin.getGame().getGamePlayer(player).getStanCooldown(), 0)) + "s";
        }
        if (params.equalsIgnoreCase("glowc")) {
            return (Math.max(plugin.getGame().getGamePlayer(player).getGlowingHintCooldown(), 0)) + "s";
        }
        if (params.equalsIgnoreCase("pTime")) {
            return df.format((double)plugin.getGame().getGamePlayer(player).getParkourTime()/20) + "s";
        }
        if (params.equalsIgnoreCase("pTimeLap")) {
            return df.format((double)plugin.getGame().getGamePlayer(player).getParkourTime2()/20) + "s";
        }
        if (params.equalsIgnoreCase("pStatus")) {
            return plugin.getGame().getGamePlayer(player).getParkourStatus().i + "";
        }
        if (params.equalsIgnoreCase("troll")) {
            return (Math.max(plugin.getGame().getGamePlayer(player).getTrollPoint(), 0)) + "";
        }
        return null;
    }

}
