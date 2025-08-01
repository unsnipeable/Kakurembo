package matanku.kakurembo.menu;

import matanku.kakurembo.enums.GameRole;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.api.menu.Button;
import matanku.kakurembo.api.menu.Menu;
import matanku.kakurembo.api.menu.button.IntegerButton;
import matanku.kakurembo.api.menu.button.ToggleButton;
import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.api.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.game.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameSettingsMenu extends Menu {
    @Override
    public Component getTitle(Player player) {
        return Common.text("ゲーム内設定");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        final Game game = HideAndSeek.INSTANCE.getGame();

        buttons.put(0, new ToggleButton() {
            @Override
            public String getOptionName() {
                return "<green>ロールを選択";
            }

            @Override
            public String getDescription() {
                return "<gray>プレイヤーがハイダーかシーカーを選択できるようにすべきでしょうか?";
            }

            @Override
            public boolean isEnabled(Player player) {
                return game.getSettings().isAllowPublicRoleSelect();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                game.getSettings().setAllowPublicRoleSelect(!game.getSettings().isAllowPublicRoleSelect());
                if (isEnabled(player)) {
                    Common.broadcastMessage("<white>管理者によりロールの選択が<green>許可<white>されました！ ", "<aqua>/roleselect<white>を使用してロールを選択することができます！");
                } else {
                    Common.broadcastMessage("<white>管理者によりロールの選択が<red>禁止<white>されました！ ", "<white>全員のロールがリセットされました！");
                    for (Map.Entry<UUID, GamePlayer> entry : game.getPlayers().entrySet()) {
                        entry.getValue().setRole(GameRole.NONE);
                    }
                }
                openMenu(player);
            }
        });

        buttons.put(1, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.MAP).name("<green><bold>マップ").lore("","<gray>現在のマップ: " + (game.getSettings().getMap() == null ? "<red>未設定" : "<green>" + game.getSettings().getMap()),"","<yellow>クリックしてマップを選択").build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new MapSelectMenu(GameSettingsMenu.this).openMenu(player);
            }
        });

        buttons.put(2, new IntegerButton() {
            @Override
            public Material getMaterial() {
                return Material.PLAYER_HEAD;
            }

            @Override
            public String getOptionName() {
                return "鬼の人数の最大";
            }

            @Override
            public String getDescription() {
                return "鬼側の最大数を設定する";
            }

            @Override
            public String getCurrentValue() {
                return game.getSettings().getMaxSeekers() == -1 ? "<red>未設定" : game.getSettings().getMaxSeekers() + "";
            }

            @Override
            public void plus1(Player player) {
                game.getSettings().setMaxSeekers(Math.max(1, game.getSettings().getMaxSeekers() + 1));
            }

            @Override
            public void plus10(Player player) {
                game.getSettings().setMaxSeekers(Math.max(1, game.getSettings().getMaxSeekers() + 10));
            }

            @Override
            public void minus1(Player player) {
                game.getSettings().setMaxSeekers(Math.max(1, game.getSettings().getMaxSeekers() - 1));
            }

            @Override
            public void minus10(Player player) {
                game.getSettings().setMaxSeekers(Math.max(1, game.getSettings().getMaxSeekers() - 10));
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                super.clicked(player, clickType);
                openMenu(player);
            }
        });

        buttons.put(3, new ToggleButton() {
            @Override
            public String getOptionName() {
                return "<green>サーバーの途中参加";
            }

            @Override
            public String getDescription() {
                return "<gray>プレイヤーがサーバーに途中参加した際、接続を拒否するのではなく、シーカーとして参加させるべきでしょうか？";
            }

            @Override
            public boolean isEnabled(Player player) {
                return game.getSettings().isAllowJoinAsSeekerAfterStarted();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                game.getSettings().setAllowJoinAsSeekerAfterStarted(!game.getSettings().isAllowJoinAsSeekerAfterStarted());
                openMenu(player);
            }
        });

        buttons.put(4, new ToggleButton() {
            @Override
            public String getOptionName() {
                return "<green>完全でないブロックに変身";
            }

            @Override
            public String getDescription() {
                return "<gray>プレイヤーがチェストなど、横の当たり判定が完全でないブロックに変身できるようにすべきでしょうか?";
            }

            @Override
            public boolean isEnabled(Player player) {
                return game.getSettings().isAllowUnbalancedBlocks();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                game.getSettings().setAllowUnbalancedBlocks(!game.getSettings().isAllowUnbalancedBlocks());
                if (isEnabled(player)) {
                    Common.broadcastMessage("<gray>[<yellow>!<gray>]<white>管理者により横の当たり判定が完全でないブロックに変身することが<green>許可<white>されました!", "<aqua>チェスト<white>、<aqua>ハーフブロック<white>などに変身することができます!");
                } else {
                    Common.broadcastMessage("<gray>[<yellow>!<gray>]<white>管理者により横の当たり判定が完全でないブロックに変身することが<red>禁止<white>されました!");

                }
                openMenu(player);
            }
        });
        buttons.put(7, new Button() {
            @Override
            public String getOptionName() {
                return "<green>プレイヤーのロールを変更する";
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.MACE).name(getOptionName()).enchantment(Enchantment.UNBREAKING, 1).lore(getDescription()).itemFlag(ItemFlag.HIDE_ENCHANTS).build();
            }

            @Override
            public String getDescription() {
                return "<gray>クリックして全てのプレイヤーのロールを編集します。";
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new RoleSelectAdminMenu().openMenu(player);
                player.closeInventory();
            }
        });
        buttons.put(8, new Button() {
            @Override
            public String getOptionName() {
                return "<green>ゲームを開始する";
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.LIME_DYE).name(getOptionName()).enchantment(Enchantment.UNBREAKING, 1).lore(getDescription()).itemFlag(ItemFlag.HIDE_ENCHANTS).build();
            }

            @Override
            public String getDescription() {
                return "<gray>さあ、クリックしてゲームを始めましょう!";
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                HideAndSeek.INSTANCE.getGame().startCountdown(10);
                player.closeInventory();
            }
        });
        return buttons;
    }
}
