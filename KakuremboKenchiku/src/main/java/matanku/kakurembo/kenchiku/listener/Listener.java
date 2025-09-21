package matanku.kakurembo.kenchiku.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import matanku.kakurembo.api.KakuremboAPI;
import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.kenchiku.Kenchiku;
import matanku.kakurembo.kenchiku.player.BuildPlayer;
import matanku.kakurembo.kenchiku.player.PlayerUtil;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Listener implements org.bukkit.event.Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Kenchiku.getINSTANCE().getPlayers().putIfAbsent(player.getUniqueId(), new BuildPlayer(player.getUniqueId()));

        PlayerUtil.reset(player);
        player.teleport(new Location(Bukkit.getWorld("world"),8,-59,8,180,0));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Kenchiku.getINSTANCE().getPlayers().remove(player.getUniqueId());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
        if (event.getBlock().getLocation().getWorld().getName().contains("world") && !player.getName().contains("matanku")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
        if (event.getBlock().getLocation().getWorld().getName().contains("world") && !player.getName().contains("matanku")) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String message = LegacyComponentSerializer.legacySection().serialize(event.message());

        event.setCancelled(true);
        Common.broadcastMessage("<gray>[" + player.getWorld().getName() + "] <gold>[BUILDER] " + player.getName() + "<white>: " + message);
    }
}
