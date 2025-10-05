package matanku.kakurembo.menu.impl;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.menu.Button;
import matanku.kakurembo.menu.Menu;
import matanku.kakurembo.menu.button.IntegerButton;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.enums.ConfigurableTimes;
import matanku.kakurembo.game.Game;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class TimeSetMenu extends Menu {

    @Override
    public Component getTitle(Player player) {
        return Common.text("時間設定");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        final Game game = HideAndSeek.Instance.getGame();

        for (ConfigurableTimes gs : ConfigurableTimes.values()) {
            buttons.put(buttons.size(), new IntegerButton() {
                @Override
                public Material getMaterial() {
                    return Material.CLOCK;
                }

                @Override
                public String getOptionName() {
                    return gs.displayName;
                }

                @Override
                public String[] getDescription() {
                    return new String[]{gs.desc};
                }

                @Override
                public String getCurrentValue() {
                    return game.getSettings().getTimes().getOrDefault(gs.id,-1) == -1 ? "<red>未設定" : ((gs.nokori?"残り":"") + game.getSettings().getTimes().getOrDefault(gs.id,-1) + "秒");
                }

                @Override
                public void plus1(Player player) {
                    game.getSettings().getTimes().put(gs.id,Math.max(0, game.getSettings().getTimes().getOrDefault(gs.id,-1) + 1));
                }

                @Override
                public void plus10(Player player) {
                    game.getSettings().getTimes().put(gs.id,Math.max(0, game.getSettings().getTimes().getOrDefault(gs.id,-1) + 10));
                }

                @Override
                public void minus1(Player player) {
                    game.getSettings().getTimes().put(gs.id,Math.max(0, game.getSettings().getTimes().getOrDefault(gs.id,-1) - 1));
                }

                @Override
                public void minus10(Player player) {
                    game.getSettings().getTimes().put(gs.id,Math.max(0, game.getSettings().getTimes().getOrDefault(gs.id,-1) - 10));
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    super.clicked(player, clickType);
                    openMenu(player);
                }
            });
        }

        return buttons;
    }
}
