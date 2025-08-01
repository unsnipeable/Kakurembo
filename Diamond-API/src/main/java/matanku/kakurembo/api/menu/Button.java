package matanku.kakurembo.api.menu;

import matanku.kakurembo.api.menu.button.IntegerButton;
import matanku.kakurembo.api.menu.button.ToggleButton;
import matanku.kakurembo.api.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class Button {
    public String getOptionName() {return null;}
    public String getDescription() {return null;}

    public int guiSlot;
    public Material getMaterial() {
        return null;
    }
    public ItemStack getButtonItem(Player player) {
        String value = "";
        if (this instanceof ToggleButton tButton) {
            value = tButton.isEnabled(player) ? "<green>" : "<red>" + tButton.isEnabled(player);
            return new ItemBuilder(getMaterial()).name(getOptionName()).lore(getDescription(), "<red>", value).build();
        } else if (this instanceof IntegerButton iButton) {
            value = "<yellow>" + iButton.getCurrentValue();
            return new ItemBuilder(getMaterial()).name(getOptionName()).lore(getDescription(), "<red>", value).build();
        }
        return new ItemBuilder(getMaterial()).name(getOptionName()).lore(getDescription()).build();
    }

    public void clicked(Player player, ClickType clickType) {
        player.getOpenInventory().getTopInventory().setItem(guiSlot,getButtonItem(player));
        player.getOpenInventory().getTopInventory().setItem(guiSlot,getButtonItem(player));
    }
}
