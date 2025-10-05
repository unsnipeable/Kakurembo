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
        Registers.menus.add(this);
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

        GamePlayer gamePlayer = HideAndSeek.getInstance().getGame().getGamePlayer(player);
        gamePlayer.setMenu(this);
    }


    public int getSize(Player player) {
        int[] invSizes = {9,18,27,36,45,54};
        for (int i : invSizes) {
            if (getButtons(player).size() <= i) return i;
        }
        return 54;
    }
}