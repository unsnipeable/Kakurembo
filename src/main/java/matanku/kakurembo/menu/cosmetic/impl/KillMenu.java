package matanku.kakurembo.menu.cosmetic.impl;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.menu.Button;
import matanku.kakurembo.menu.Menu;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.config.Messages;
import matanku.kakurembo.config.datamanager.Manager;
import matanku.kakurembo.config.datamanager.impl.CosmeticDataManager;
import matanku.kakurembo.enums.GameRole;
import matanku.kakurembo.player.GamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class KillMenu extends Menu {

    @Override
    public Component getTitle(Player player) {
        return Common.text("コスメティックス");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        for (Messages.Message gs : Messages.Message.values()) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public Material getMaterial() {
                    return gs.getIcon().getType();
                }

                @Override
                public String getOptionName() {
                    return "<light_purple>" + gs.getTitle() + " キルメッセージ";
                }

                @Override
                public String[] getDescription() {
                    String selected = gs.getMessage();
                    Map<String, String> replaces = new HashMap<>() {{
                        put("player","<gray>" + player.getName());
                        put("attacker","<gray>seeker");
                        put("seeker", GameRole.SEEKER.getColoredName() + "<gray>");
                        put("hider", GameRole.HIDER.getColoredName() + "<gray>");
                    }};
                    for (Map.Entry<String, String> entry : replaces.entrySet()) {
                        selected = selected.replace("%" + entry.getKey() + "%", entry.getValue());
                    }
                    GamePlayer gamePlayer = HideAndSeek.Instance.getGame().getGamePlayer(player);
                    boolean isSelected = (gamePlayer.getSelectedKillMessage() == gs);
                    List<Messages.Message> unlocked = gamePlayer.getUnlockedKillMessage();
                    String checkMessage = isSelected ? ("<green>選択済み"):(unlocked.contains(gs) ? "<aqua>解放済み" : "<red>まだ解放されていません");
                    String coin = "<gold>" + gs.getUnlockCoin() + "コインで解放";
                    return new String[]{selected, "", coin,checkMessage};
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    GamePlayer gamePlayer = HideAndSeek.Instance.getGame().getGamePlayer(player);
                    List<Messages.Message> unlocked = gamePlayer.getUnlockedKillMessage();
                    if (unlocked.contains(gs)) {
                        HideAndSeek.Instance.getGame().getGamePlayer(player).setSelectedKillMessage(gs);
                        Common.sendMessage(player, "<green>" +gs.getTitle()+" キルメッセージを選択しました。");
                    } else {
                        if (gamePlayer.getCoin() >= gs.getUnlockCoin()) {
                            gamePlayer.setCoin(gamePlayer.getCoin() - gs.getUnlockCoin());
                            gamePlayer.unlockedKillMessage.add(gs);
                            Common.sendMessage(player, "<green>" +gs.getTitle()+" キルメッセージを購入しました。");
                        } else {
                            Common.sendMessage(player, "<red>コインが足りません。");
                        }
                    }

                    StringBuilder u = new StringBuilder();
                    for (Messages.Message msg : Messages.Message.values()) {
                        u.append(gamePlayer.unlockedKillMessage.contains(msg) ? "T" : "F");
                    }
                    Manager.getDataManager(CosmeticDataManager.class).addPlayerInfoString(player.getName(), u.toString());
                    reloadMenu(player);
                }
            });
        }

        return buttons;
    }

    public int getSize() {
        return 27;
    }
}
