package matanku.kakurembo.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import matanku.kakurembo.enums.ChatEnum;
import matanku.kakurembo.enums.CheckPointStatus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    private int flagged = 0;
    private boolean enableBuild;
    private ChatEnum FocusedChat = ChatEnum.ALL;
    private Party party = null;
    @Setter
    private DisguiseData disguises;

    private boolean parkour = false;
    private int parkourTime;
    private int parkourTime2;
    private Location parkourSpawn = null;
    private Location checkPoint = null;
    private CheckPointStatus parkourStatus = CheckPointStatus.NOTPLAYING;

    public void onTickParkour() {
        if (parkour) {
            parkourTime++;
            parkourTime2++;
        } else {
            setParkourTime(0);
            setParkourTime2(0);
        }
    }

    public boolean isPartyLeader() {
        if (party == null) return false;
        return party.getLeader() == this;
    }

    public void setParkour(boolean bool) {
        if (!bool) {
            getPlayer().getInventory().clear();
        }
        parkour = bool;
    }

    public void reset() {
        role = GameRole.NONE;
        disguises = null;
    }

    public void parkourLap() {
        setParkourTime2(0);
    }


    public Player getPlayer() {
        return Bukkit.getPlayer(uniqueID);
    }

    public boolean isOnline() {
        return getPlayer() != null && getPlayer().isOnline();
    }

}
