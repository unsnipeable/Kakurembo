package matanku.kakurembo.command.game;

import matanku.kakurembo.command.ServerCommand;
import matanku.kakurembo.menu.impl.GameSettingsMenu;
import matanku.kakurembo.util.enums.RankEnum;
import org.bukkit.entity.Player;

public class RoleMenu extends ServerCommand {
    public RoleMenu() {
        super("rolemenu");
        this.setPerm(RankEnum.MVP);
    }

    @Override
    public void run(Player player, String[] args, String command) {
        new GameSettingsMenu().openMenu(player);
    }
}
