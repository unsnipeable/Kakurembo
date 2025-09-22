package matanku.kakurembo.menu;

import matanku.kakurembo.enums.ConfigurableTimes;
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

import java.util.ArrayList;
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
        final Game game = HideAndSeek.Instance.getGame();

        buttons.put(0, new ToggleButton() {
            @Override
            public String getOptionName() {
                return "ロールを選択";
            }

            @Override
            public String[] getDescription() {
                return new String[]{"<gray>プレイヤーが/roleselectを使用して","ハイダーかシーカーを選択できるようにすべきでしょうか?"};
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


        buttons.put(1, new IntegerButton() {
            @Override
            public Material getMaterial() {
                return Material.DIAMOND_SWORD;
            }

            @Override
            public String getOptionName() {
                return "鬼の人数の最大";
            }

            @Override
            public String[] getDescription() {
                return new String[]{"鬼側の最大数を設定する"};
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

        buttons.put(2, new ToggleButton() {

            @Override
            public Material getMaterial() {
                return Material.OAK_DOOR;
            }

            @Override
            public String getOptionName() {
                return "サーバーの途中参加";
            }

            @Override
            public String[] getDescription() {
                return new String[]{"<gray>プレイヤーがサーバーに途中参加した際接続を拒否するのではなく","シーカーとして参加させるべきでしょうか？"};
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

        buttons.put(3, new ToggleButton() {
            @Override
            public String getOptionName() {
                return "完全でないブロックに変身";
            }

            @Override
            public Material getMaterial() {
                return Material.OAK_STAIRS;
            }

            @Override
            public String[] getDescription() {
                return new String[]{"<gray>プレイヤーがチェストなど","横の当たり判定が完全でないブロックに変身できるようにすべきでしょうか?"};
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

        buttons.put(4, new ToggleButton() {
            @Override
            public String getOptionName() {
                return "アンチチート";
            }

            @Override
            public Material getMaterial() {
                return Material.REDSTONE_BLOCK;
            }

            @Override
            public String[] getDescription() {
                return new String[]{"<gray>板ガラスをしようした壁抜け","看板や頭ブロックによる透明化","などのチートを無効化。"};
            }

            @Override
            public boolean isEnabled(Player player) {
                return game.getSettings().isAntiCheatEnabled();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                game.getSettings().setAntiCheatEnabled(!isEnabled(player));
                openMenu(player);
            }
        });

        buttons.put(5, new IntegerButton() {
            @Override
            public Material getMaterial() {
                return Material.STONE_SWORD;
            }

            @Override
            public String getOptionName() {
                return "シーカーにするアンチチートフラグ回数";
            }


            @Override
            public String[] getDescription() {
                return new String[]{"壁抜けなどを行おうとする回数がこの設定の回数を超えるとシーカーになります。","0回にするとシーカー化を無効化します。"};
            }

            @Override
            public String getCurrentValue() {
                return game.getSettings().getMaxFlag() == -1 ? "<red>未設定" : game.getSettings().getMaxFlag() + "";
            }

            @Override
            public void plus1(Player player) {
                game.getSettings().setMaxFlag(Math.max(0, game.getSettings().getMaxFlag() + 1));
            }

            @Override
            public void plus10(Player player) {
                game.getSettings().setMaxFlag(Math.max(0, game.getSettings().getMaxFlag() + 10));
            }

            @Override
            public void minus1(Player player) {
                game.getSettings().setMaxFlag(Math.max(0, game.getSettings().getMaxFlag() - 1));
            }

            @Override
            public void minus10(Player player) {
                game.getSettings().setMaxFlag(Math.max(0, game.getSettings().getMaxFlag() - 10));
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                super.clicked(player, clickType);
                openMenu(player);
            }
        });

        buttons.put(6, new ToggleButton() {
            @Override
            public String getOptionName() {
                return "心臓の音";
            }

            @Override
            public Material getMaterial() {
                return Material.REDSTONE;
            }

            @Override
            public String[] getDescription() {
                return new String[]{"<gray>シーカーがハイダーに近づいたときに、心臓の音を出します。"};
            }

            @Override
            public boolean isEnabled(Player player) {
                return game.getSettings().isHeartBeatEnabled();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                game.getSettings().setHeartBeatEnabled(!isEnabled(player));
                openMenu(player);
            }
        });


        buttons.put(7, new ToggleButton() {
            @Override
            public String getOptionName() {
                return "トラッカー";
            }


            @Override
            public Material getMaterial() {
                return Material.COMPASS;
            }


            @Override
            public String[] getDescription() {
                return new String[]{"<gray>残り3分になった時、シーカーにハイダーの居場所を知らせます。"};
            }

            @Override
            public boolean isEnabled(Player player) {
                return game.getSettings().isTrackerEnabled();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                game.getSettings().setTrackerEnabled(!isEnabled(player));
                openMenu(player);
            }
        });

        buttons.put(8, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.IRON_SWORD).name("<green>剣の種類").lore("","<gray>種類: " + (game.getSettings().getSwordType() == null ? "<red>未設定" : "<green>" + game.getSettings().getSwordType().getName() + "<yellow> (CLICK)"),"","<gray>火属性: " + (game.getSettings().isSwordFire() ? "<green>有効" : "<red>無効") + "<yellow> (LSHIFT + CLICK)").enchantment(Enchantment.FIRE_ASPECT, 1).build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                switch (clickType) {
                    case LEFT:
                        game.getSettings().setSwordType(game.getSettings().getSwordType().next());
                        break;
                    case SHIFT_LEFT:
                        game.getSettings().setSwordFire(!game.getSettings().isSwordFire());
                        break;
                }
                openMenu(player);
            }
        });

        buttons.put(9, new ToggleButton() {
            @Override
            public String getOptionName() {
                return "インスタキル";
            }

            @Override
            public Material getMaterial() {
                return Material.GOLDEN_SWORD;
            }

            @Override
            public String[] getDescription() {
                return new String[]{"<gray>シーカーが1発でハイダーを倒すことができます。"};
            }

            @Override
            public boolean isEnabled(Player player) {
                return game.getSettings().isInstaKill();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                game.getSettings().setInstaKill(!isEnabled(player));
                openMenu(player);
            }
        });

        buttons.put(10, new IntegerButton(){
            @Override
            public String getCurrentValue() {
                return game.getSettings().getSpeed() == -1 ? "<red>未設定" : game.getSettings().getSpeed() + "";
            }

            @Override
            public void plus1(Player player) {
                game.getSettings().setSpeed(Math.max(0, game.getSettings().getSpeed() + 1));
            }

            @Override
            public void plus10(Player player) {
                game.getSettings().setSpeed(Math.max(0, game.getSettings().getSpeed() + 10));
            }

            @Override
            public void minus1(Player player) {
                game.getSettings().setSpeed(Math.max(0, game.getSettings().getSpeed() - 1));
            }

            @Override
            public void minus10(Player player) {
                game.getSettings().setSpeed(Math.max(0, game.getSettings().getSpeed() - 10));
            }
            @Override
            public Material getMaterial() {
                return Material.SUGAR;
            }

            @Override
            public String getOptionName() {
                return "スピード";
            }

            @Override
            public String[] getDescription() {
                return new String[]{"速さのエフェクト"};
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                super.clicked(player, clickType);
                openMenu(player);
            }
        });

        buttons.put(23, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.MAP).name("<green><bold>マップ").lore("","<gray>現在のマップ: " + (game.getSettings().getMap() == null ? "<red>未設定" : "<green>" + game.getSettings().getMap()),"","<yellow>クリックしてマップを選択").build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new MapSelectMenu(GameSettingsMenu.this).openMenu(player);
            }
        });
        buttons.put(24, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                ArrayList<String> lore = new ArrayList<>();
                lore.add("");
                for (ConfigurableTimes ct : ConfigurableTimes.values()) {
                    lore.add("<gray>" + ct.displayName + ": <gold>" + game.getSettings().getTimes().get(ct.id) + "秒");
                }
                lore.add("");
                lore.add("<yellow>クリックして時間設定を変更");

                return new ItemBuilder(Material.CLOCK).name("<green><bold>時間設定").lore(lore).build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new TimeSetMenu().openMenu(player);
            }
        });

        buttons.put(25, new Button() {
            @Override
            public String getOptionName() {
                return "プレイヤーのロールを変更する";
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.MACE).name(getOptionName()).enchantment(Enchantment.UNBREAKING, 1).lore(getDescription()).itemFlag(ItemFlag.HIDE_ENCHANTS).build();
            }

            @Override
            public String[] getDescription() {
                return new String[]{"<gray>クリックして全てのプレイヤーのロールを編集します。"};
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new RoleSelectAdminMenu().openMenu(player);
            }
        });
        buttons.put(26, new Button() {
            @Override
            public String getOptionName() {
                return "ゲームを開始する";
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.LIME_DYE).name(getOptionName()).enchantment(Enchantment.UNBREAKING, 1).lore(getDescription()).itemFlag(ItemFlag.HIDE_ENCHANTS).build();
            }

            @Override
            public String[] getDescription() {
                return new String[]{"<gray>さあ、クリックしてゲームを始めましょう!"};
            }

            @Override
            public void clicked(Player player, ClickType clickType) {

                if (HideAndSeek.Instance.getGame().getSettings().getMap() == null) {
                    Common.sendMessage(player, "<red>カウントダウンを開始しようとしたときにエラーが発生しました: マップがまだ選択されていません!");
                    new MapSelectMenu(new GameSettingsMenu()).openMenu(player);
                    return;
                }

                HideAndSeek.Instance.getGame().startCountdown(10);
                player.closeInventory();
            }
        });
        return buttons;
    }

    @Override
    public int getSize() {
        return 27;
    }
}
