package matanku.kakurembo.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import matanku.kakurembo.config.Messages;
import matanku.kakurembo.config.datamanager.Manager;
import matanku.kakurembo.enums.CheckPointStatus;
import matanku.kakurembo.enums.DataEnum;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import matanku.kakurembo.enums.GameRole;
import matanku.kakurembo.game.disguise.DisguiseData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class GamePlayer {
    // constructor
    private final UUID uniqueID;

    // player
    private boolean muted;
    public String previousChat = null;
    private boolean enableBuild;

    // game
    private GameRole role = GameRole.NONE;
    private int stanCooldown = 0;
    private int flagged = 0;
    private DisguiseData disguises;
    public List<Messages.Message> unlockedKillMessage = new ArrayList<>(Arrays.stream(Messages.Message.values()).filter(Messages.Message::isUnlockedAsDefault).toList());
    private Messages.Message selectedKillMessage = Messages.Message.DEFAULT;
    private int coin;
    private int trollPoint = 0;
    private int glowingHintCooldown = 0;

    // parkour
    private boolean parkour = false;
    private int parkourTime;
    private int parkourTime2;
    private Location parkourSpawn = null;
    private Location checkPoint = null;
    private CheckPointStatus parkourStatus = CheckPointStatus.NOTPLAYING;

    public void setCoin(int i) {
        coin = i;

        Manager.getDataManager("CoinDataManager").addPlayerInfoInteger(getPlayer().getUniqueId().toString(), i, DataEnum.DataManagerType.SET);

    }

    public void onTickParkour() {
        if (parkour) {
            parkourTime++;
            parkourTime2++;
        } else {
            setParkourTime(0);
            setParkourTime2(0);
        }
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
