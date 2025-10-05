package matanku.kakurembo.menu.impl;

import lombok.RequiredArgsConstructor;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.util.ItemBuilder;
import matanku.kakurembo.menu.Button;
import matanku.kakurembo.menu.Menu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.game.Game;

import java.io.File;
import java.util.*;

@RequiredArgsConstructor
public class MapSelectMenu extends Menu {

    private final Menu backMenu;

    @Override
    public Component getTitle(Player player) {
        return Common.text("マップ選択");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        final Game game = HideAndSeek.Instance.getGame();
        final List<File> mapFolder = Arrays.stream(Objects.requireNonNull(new File("plugins/" + HideAndSeek.Instance.getDescription().getName() + "/maps/").listFiles())).filter(File::isDirectory).toList();
        final ConfigurationSection mapsSelection = HideAndSeek.Instance.getMapFile().getConfiguration().getConfigurationSection("maps");

        assert mapsSelection != null;

        for (File file : mapFolder) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.MAP).name("<green>" + file.getName()).build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    if (mapsSelection.contains(file.getName())) {
                        game.getSettings().setMap(file.getName());
                        backMenu.openMenu(player);
                    }
                }
            });
        }

        return buttons;
    }
}
