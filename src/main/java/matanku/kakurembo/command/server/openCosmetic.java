package matanku.kakurembo.command.server;

import matanku.kakurembo.command.ServerCommand;
import matanku.kakurembo.menu.cosmetic.CosmeticMenu;
import org.bukkit.entity.Player;

public class openCosmetic extends ServerCommand {
    public openCosmetic() {
        super("cosmeticmenu");
    }

    @Override
    public void run(Player player, String[] args, String command) {
        new CosmeticMenu().openMenu(player);
    }
}
