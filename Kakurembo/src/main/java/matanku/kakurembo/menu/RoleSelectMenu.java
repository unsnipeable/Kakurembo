package matanku.kakurembo.menu;

import matanku.kakurembo.enums.GameRole;
import matanku.kakurembo.game.Game;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.api.menu.Button;
import matanku.kakurembo.api.menu.Menu;
import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.api.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import matanku.kakurembo.HideAndSeek;

import java.util.HashMap;
import java.util.Map;

public class RoleSelectMenu extends Menu {

    @Override
    public Component getTitle(Player player) {
        return Common.text("ブロックを選択");
    }

    @Override
    public int getSize() {
        return 9*3;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        GamePlayer gamePlayer = HideAndSeek.INSTANCE.getGame().getGamePlayer(player);

        buttons.put(11, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.COW_SPAWN_EGG).name(GameRole.HIDER.getColoredName()).lore("<gray>ハイダーはシーカーから逃げなければなりません！", "<gray>制限時間内にシーカーに殺されなかったプレイヤーが勝者！", "", gamePlayer.getRole() == GameRole.HIDER ? "<green>選択済み" : "<yellow>クリック" + GameRole.HIDER.getColoredName()).glow(gamePlayer.getRole() == GameRole.HIDER).build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                selectRole(gamePlayer, GameRole.HIDER);
                openMenu(player);
            }
        });

        buttons.put(15, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.DIAMOND_SWORD).name(GameRole.SEEKER.getColoredName()).lore("<gray>シーカーは制限時間内にハイダー全員を見つけなければなりません！", "", gamePlayer.getRole() == GameRole.SEEKER ? "<green>選択済み" : "<yellow>クリック" + GameRole.SEEKER.getColoredName()).glow(gamePlayer.getRole() == GameRole.SEEKER).build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                selectRole(gamePlayer, GameRole.SEEKER);
                openMenu(player);
            }
        });

        return buttons;
    }

    private void selectRole(GamePlayer gamePlayer, GameRole role) {
        Game game = HideAndSeek.INSTANCE.getGame();
        Player player = gamePlayer.getPlayer();
        if (game.isStarted()) {
            Common.sendMessage(player, "<red>ゲーム開始後に自分の役職を設定することはできません");
            return;
        }
        if (!game.getSettings().isAllowPublicRoleSelect()) {
            Common.sendMessage(player, "<red>ホストがオープンチーム選択を有効にしていないため、役職を選択できません");
            return;
        }
        gamePlayer.setRole(role);
        Common.sendMessage(player, "<yellow>あなたの役職: " + role.getColoredName());
    }
}
