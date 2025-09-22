package matanku.kakurembo.api.menu;

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
        this.gui = Bukkit.createInventory(null, getSize(), getTitle(player));

        if (this.getButtons(player) != null) {
            this.getButtons(player).forEach((k, v) -> {
                v.guiSlot = k;
                gui.setItem(k, v.getButtonItem(player));
            });
        }

        player.closeInventory();
        player.openInventory(gui);
    }


    public int getSize() {
        return 9;
    }
}