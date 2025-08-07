package matanku.kakurembo.menu;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.api.menu.Button;
import matanku.kakurembo.api.menu.button.IntegerButton;
import matanku.kakurembo.api.menu.pagination.PaginatedMenu;
import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.api.util.ItemBuilder;
import matanku.kakurembo.enums.ConfigurableTimes;
import matanku.kakurembo.game.Game;
import matanku.kakurembo.player.Replay;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ReplayMenu extends PaginatedMenu {

    @Override
    public Component getPrePaginatedTitle(Player player) {
        return Common.text("リプレイ");
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        final Game game = HideAndSeek.INSTANCE.getGame();

        for (Replay replay : game.getGamePlayer(player).getPlayerReplay()) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.BOOK).name("Replay (" + replay.getReplay().get(0).get(player.getName()).world() + ")").build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    replay.generateWorld(game.getGamePlayer(player));
                }
            });
        }

        return buttons;
    }
}
