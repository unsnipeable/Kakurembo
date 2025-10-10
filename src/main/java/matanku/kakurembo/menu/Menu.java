package matanku.kakurembo.menu;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.player.GamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public class Menu {

    public Inventory gui;
    public Menu() {
        register();
    }

    public void register() {
        MenuManager.menus.add(this);
    }


    public Map<Integer, Button> getButtons(Player player) {return null;}
    public Component getTitle(Player player) {
        return null;
    }

    public void openMenu(Player player) {
        this.gui = Bukkit.createInventory(null, getSize(player), getTitle(player));

        if (this.getButtons(player) != null) {
            this.getButtons(player).forEach((k, v) -> {
                v.guiSlot = k;
                gui.setItem(k, v.getButtonItem(player));
            });
        }

        player.closeInventory();
        player.openInventory(gui);

        HideAndSeek.getInstance().getGame().getGamePlayer(player).setMenu(this);
    }

    public void reloadMenu(Player player) {
        player.getOpenInventory().getTopInventory().clear();
        if (this.getButtons(player) != null) {
            this.getButtons(player).forEach((k, v) -> {
                v.guiSlot = k;
                player.getOpenInventory().getTopInventory().setItem(k, v.getButtonItem(player));
            });
        }
        this.gui = player.getOpenInventory().getTopInventory();
    }


    public int getSize(Player player) {
        if (getButtons(player) == null || getButtons(player).isEmpty()) {
            return 9;
        }

        int highestSlot = getButtons(player).keySet().stream().max(Integer::compareTo).orElse(0) + 1;

        int[] invSizes = {9,18,27,36,45,54};
        for (int size : invSizes) {
            if (highestSlot <= size) return size;
        }
        return 54;
    }
}