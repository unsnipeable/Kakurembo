package matanku.kakurembo.menu;

import matanku.kakurembo.enums.DisguiseTypes;
import matanku.kakurembo.game.disguise.DisguiseData;
import matanku.kakurembo.api.menu.Button;
import matanku.kakurembo.api.menu.pagination.PaginatedMenu;
import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.api.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.game.Game;

import java.util.HashMap;
import java.util.Map;

public class DisguiseSelectMenu extends PaginatedMenu {
    @Override
    public Component getPrePaginatedTitle(Player player) {
        return Common.text("変装の選択");
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        final Game game = HideAndSeek.INSTANCE.getGame();

        if (game.getSettings().getMap() == null) {
            return buttons;
        }

        for (String block : HideAndSeek.INSTANCE.getMapFile().getStringList("maps." + game.getSettings().getMap() + ".disguises.blocks")) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.valueOf(block)).name("<green>" + block).lore("", "<yellow>偽装するにはブロックにクリックしてください!").build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    game.getGamePlayer(player).setDisguises(new DisguiseData(DisguiseTypes.BLOCK, block));
                    player.closeInventory();
                }
            });
        }

        for (String mob : HideAndSeek.INSTANCE.getMapFile().getStringList("maps." + game.getSettings().getMap() + ".disguises.mobs")) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.valueOf(mob + "_SPAWN_EGG")).name("<green>" + mob).lore("", "<yellow>変装するにはMOBにクリックしてください!").build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    game.getGamePlayer(player).setDisguises(new DisguiseData(DisguiseTypes.MOB, mob));
                    player.closeInventory();
                }
            });
        }

        return buttons;
    }
}
