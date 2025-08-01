package matanku.kakurembo.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import matanku.kakurembo.enums.GameRole;
import matanku.kakurembo.game.disguise.DisguiseData;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class GamePlayer {

    private final UUID uniqueID;
    private GameRole role = GameRole.NONE;
    private int stanCooldown = 0;
    @Setter
    private DisguiseData disguises;

    public void reset() {
        role = GameRole.NONE;
        disguises = null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uniqueID);
    }

    public boolean isOnline() {
        return getPlayer() != null && getPlayer().isOnline();
    }

}
