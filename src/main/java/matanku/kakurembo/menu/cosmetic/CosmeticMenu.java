package matanku.kakurembo.menu.cosmetic;

import matanku.kakurembo.menu.Button;
import matanku.kakurembo.menu.Menu;
import matanku.kakurembo.menu.cosmetic.impl.KillMenu;
import matanku.kakurembo.util.Common;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.Map;

public class CosmeticMenu extends Menu {

    @Override
    public Component getTitle(Player player) {
        return Common.text("コスメティックス");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        // OOWOWOWOO
        buttons.put(4,new Button() {
            @Override
            public Material getMaterial() {
                return Material.IRON_SWORD;
            }

            @Override
            public String getOptionName() {
                return "<red>キルメッセージ";
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new KillMenu().openMenu(player);
            }
        });

        return buttons;
    }

}
