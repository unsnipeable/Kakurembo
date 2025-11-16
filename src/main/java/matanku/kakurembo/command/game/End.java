package matanku.kakurembo.command.game;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.command.ServerCommand;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.util.enums.RankEnum;
import org.bukkit.entity.Player;

public class End extends ServerCommand {
    public End() {
        super("end");
        this.setPerm(RankEnum.ADMIN);
    }

    @Override
    public void run(Player player, String[] args, String command) {
        try {
            HideAndSeek.getInstance().getGame().end();
        } catch (AssertionError error) {
            Common.sendMessage(player, Common.text("<red>" + error.getMessage()));
        }
    }
}
