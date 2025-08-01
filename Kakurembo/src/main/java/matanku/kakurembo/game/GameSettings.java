package matanku.kakurembo.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSettings {

    private boolean allowPublicRoleSelect = false;
    private String map = null;
    private int maxSeekers = -1;
    private boolean allowJoinAsSeekerAfterStarted = true;
    private boolean allowUnbalancedBlocks = false;

}
