package matanku.kakurembo.api.menu.pagination;

import matanku.kakurembo.api.menu.Button;
import matanku.kakurembo.api.menu.Registers;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class PaginatedMenu {
    public PaginatedMenu() {
        register();
    }

    public boolean isOpened;

    public void register() {
        Registers.paginatedMenus.add(this);
    }

    public Component getPrePaginatedTitle(Player player) {
        return null;
    }
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        return new HashMap<>();
    }

    public void openMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, this.getPrePaginatedTitle(player));

        this.getAllPagesButtons(player).forEach((k, v) -> {gui.setItem(k, v.getButtonItem(player));
            v.guiSlot = k;});
        this.getGlobalButtons(player).forEach((k, v) -> {gui.setItem(k, v.getButtonItem(player));
             v.guiSlot = k;});


        player.openInventory(gui);
        isOpened = true;
    }

    public Map<Integer, Button> getGlobalButtons(Player player) {
        return new HashMap<>();
    }
}
