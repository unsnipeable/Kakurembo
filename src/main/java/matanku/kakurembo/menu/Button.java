package matanku.kakurembo.menu;

import matanku.kakurembo.menu.button.IntegerButton;
import matanku.kakurembo.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class Button {
    public String getOptionName() {return "";}
    public String[] getDescription() {return new String[]{""};}

    public int guiSlot;
    public Material getMaterial() {
        return null;
    }
    public ItemStack getButtonItem(Player player) {
        String value = "";
        if (this instanceof IntegerButton iButton) {
            value = "<gray>現在の値: <yellow>" + iButton.getCurrentValue();
            ArrayList<String> a = new ArrayList<>(Arrays.asList(getDescription()));
            a.add("<red>");
            a.add(value);
            return new ItemBuilder(getMaterial()).name(getOptionName()).lore(a).build();
        }
        return new ItemBuilder(getMaterial()).name(getOptionName()).lore(getDescription()).build();
    }

    public void clicked(Player player, ClickType clickType) {
        Inventory inv = player.getOpenInventory().getTopInventory();
        if (guiSlot >= 0 && guiSlot < inv.getSize()) {
            inv.setItem(guiSlot, getButtonItem(player));
        } else {
            Bukkit.getLogger().warning("Invalid guiSlot: " + guiSlot + " for " + player.getName());
        }
    }
}
