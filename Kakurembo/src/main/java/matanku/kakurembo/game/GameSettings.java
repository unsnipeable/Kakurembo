package matanku.kakurembo.game;

import lombok.Getter;
import lombok.Setter;
import matanku.kakurembo.enums.SwordType;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class GameSettings {

    private boolean allowPublicRoleSelect = false;
    private String map = null;
    private SwordType swordType = SwordType.NETHERITE;
    private int maxSeekers = -1;
    private int maxFlag = 20;
    private boolean allowJoinAsSeekerAfterStarted = true;
    private boolean allowUnbalancedBlocks = true;
    private boolean trackerEnabled = true;
    private boolean heartBeatEnabled = true;
    private boolean antiCheatEnabled = true;
    private boolean swordFire = false;
    private boolean instaKill = false;

    private Map<String, Integer> times = new HashMap<>();
}
