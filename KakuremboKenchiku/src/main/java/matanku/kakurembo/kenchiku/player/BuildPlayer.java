package matanku.kakurembo.kenchiku.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class BuildPlayer {
    private final UUID uniqueID;
    public Player getPlayer() {
        return Bukkit.getPlayer(uniqueID);
    }
    public boolean isOnline() {
        return getPlayer() != null && getPlayer().isOnline();
    }

    public Location beforeSpectate = null;
    public ItemStack[] beforeSpectateInv = null;
    public ItemStack[] beforeSpectateArm = null;
}
