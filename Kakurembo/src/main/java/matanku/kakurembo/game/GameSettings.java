package matanku.kakurembo.game;

import lombok.Getter;
import lombok.Setter;
import matanku.kakurembo.enums.SwordType;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class GameSettings {

    private boolean allowPublicRoleSelect = false;
    private String map = null;
    private SwordType swordType = SwordType.NETHERITE;
    private int maxSeekers = 1;
    private int maxFlag = 0;
    private boolean allowJoinAsSeekerAfterStarted = true;
    private boolean allowUnbalancedBlocks = true;
    private boolean trackerEnabled = true;
    private boolean heartBeatEnabled = true;
    private boolean antiCheatEnabled = true;
    private boolean swordFire = false;
    private boolean instaKill = false;
    private int speed = 0;

    private Map<String, Integer> times = new HashMap<>() {{
        put("hider_time", 30);
        put("game_time", 600);
        put("tracker_time", 180);
        put("glowing_time", 60);
        put("stun_cooldown", 60);
    }};
}
