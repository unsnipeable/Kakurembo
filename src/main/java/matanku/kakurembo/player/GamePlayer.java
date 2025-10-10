package matanku.kakurembo.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import matanku.kakurembo.config.Messages;
import matanku.kakurembo.config.datamanager.Manager;
import matanku.kakurembo.config.datamanager.impl.CoinDataManager;
import matanku.kakurembo.config.datamanager.impl.StarDataManager;
import matanku.kakurembo.config.datamanager.impl.StarProgressDataManager;
import matanku.kakurembo.menu.Menu;
import matanku.kakurembo.enums.CheckPointStatus;
import matanku.kakurembo.enums.DataEnum;
import matanku.kakurembo.enums.Prestige;
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
    private Menu menu;

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

    // star
    private int star;
    private int xp;
    private int xp2;

    // parkour
    private boolean parkour = false;
    private int parkourTime;
    private int parkourTime2;
    private Location parkourSpawn = null;
    private Location checkPoint = null;
    private CheckPointStatus parkourStatus = CheckPointStatus.NOTPLAYING;

    public void setParkourSpawn(Location loc) {
        Location p = loc.clone();
        p.setPitch(0f);
        parkourSpawn = p;
    }

    public void addXp(int i) {
        xp2 += i;
        int t = i + xp;
        int level = star;
        while (t > 1000) {
            t -= 1000;
            level++;
        }
        Manager.getDataManager(StarProgressDataManager.class).addPlayerInfoInteger(getPlayer().getUniqueId().toString(), t, DataEnum.DataManagerType.SET);
        Manager.getDataManager(StarDataManager.class).addPlayerInfoInteger(getPlayer().getUniqueId().toString(), level, DataEnum.DataManagerType.SET);
        xp = t;
        star = level;
    }

    public void setCoin(int i) {
        coin = i;
        Manager.getDataManager(CoinDataManager.class).addPlayerInfoInteger(getPlayer().getUniqueId().toString(), i, DataEnum.DataManagerType.SET);
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


    public String getPrestigeFormat() {
        int star = getStar();
        Prestige prestige = Arrays.stream(Prestige.values()).filter(p -> p.getStar() <= star).reduce(Prestige.DEFAULT, (a, b) -> b);
        String format = prestige.getFormat().replace("%star%", String.valueOf(star)).replace("&", "§");
        String s = String.valueOf(star);
        for (int i = 0; i < Math.min(5, s.length()); i++) {
            format = format.replace("%" + (i + 1) + "%", String.valueOf(s.charAt(i)));
        }
        return format;
    }

    public String getPrestigeFormatWithoutBracket() {
        return getPrestigeFormat().replace("[", "").replace("]", "");
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

}
