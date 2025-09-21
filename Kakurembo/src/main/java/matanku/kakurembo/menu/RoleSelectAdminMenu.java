package matanku.kakurembo.menu;

import matanku.kakurembo.api.menu.Menu;
import matanku.kakurembo.enums.GameRole;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.api.menu.Button;
import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.api.util.ItemBuilder;
import matanku.kakurembo.api.util.Symbols;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import matanku.kakurembo.HideAndSeek;

import java.util.*;

public class RoleSelectAdminMenu extends Menu {

    @Override
    public Component getTitle(Player player) {
        return Common.text("ロールを設定する");
    }

    @Override
    public Map<Integer, Button> getButtons(Player viewer) {
        final Map<Integer, Button> buttons = new HashMap<>();

        for (GamePlayer gamePlayer : HideAndSeek.INSTANCE.getGame().getPlayers().values()) {
            Player player = gamePlayer.getPlayer();
            GameRole role = gamePlayer.getRole();

            ItemBuilder builder = new ItemBuilder(Material.PLAYER_HEAD).name(role.getColor() + player.getName());
            builder.lore("");
            builder.lore(Arrays.stream(GameRole.values()).map(gr -> (role == gr ? " <green>" + Symbols.ARROW_RIGHT + " " : "   ") + gr.getColoredName()).toList());
            builder.lore("", "<yellow>クリックして切り替える: " + player.getName());

            buttons.put(9 + buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return builder.build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    int index = role.ordinal();
                    int maxIndex = GameRole.values().length;

                    if (index + 1 == maxIndex) {
                        gamePlayer.setRole(GameRole.values()[0]);
                        openMenu(player);
                        return;
                    }
                    gamePlayer.setRole(GameRole.values()[index + 1]);
                    openMenu(player);
                }
            });
        }

        buttons.put(3, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STRUCTURE_VOID).name("<yellow>" + GameRole.NONE + "人数: <aqua>" + HideAndSeek.INSTANCE.getGame().getPlayers().values().stream().filter(gp -> gp.getRole() == GameRole.NONE).count()).build();
            }
        });
        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.COW_SPAWN_EGG).name("<yellow>" + GameRole.HIDER + "人数: <aqua>" + HideAndSeek.INSTANCE.getGame().getPlayers().values().stream().filter(gp -> gp.getRole() == GameRole.HIDER).count()).build();
            }
        });
        buttons.put(5, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.DIAMOND_SWORD).name("<yellow>" + GameRole.SEEKER + "人数: <aqua>" + HideAndSeek.INSTANCE.getGame().getPlayers().values().stream().filter(gp -> gp.getRole() == GameRole.SEEKER).count()).build();
            }
        });
        return buttons;
    }

}
