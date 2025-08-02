package matanku.kakurembo.api.menu.button;

import matanku.kakurembo.api.menu.Button;
import matanku.kakurembo.api.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ToggleButton extends Button {
    public boolean isEnabled(Player player) {
        return false;
    }
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(getMaterial() == null ? (isEnabled(player) ? Material.GREEN_CONCRETE : Material.RED_CONCRETE) : getMaterial()).name((isEnabled(player) ? "<green>" : "<red>") + getOptionName()).lore(getDescription()).glow(isEnabled(player)).build();
    }
}
