package matanku.kakurembo.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSettings {

    private boolean allowPublicRoleSelect = false;
    private String map = null;
    private int maxSeekers = -1;
    private int maxFlag = 20;
    private boolean allowJoinAsSeekerAfterStarted = true;
    private boolean allowUnbalancedBlocks = true;
    private boolean trackerEnabled = true;
    private boolean heartBeatEnabled = true;
    private boolean antiCheatEnabled = true;

}
