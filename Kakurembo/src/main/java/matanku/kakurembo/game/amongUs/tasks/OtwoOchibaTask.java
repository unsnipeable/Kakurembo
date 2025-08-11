package matanku.kakurembo.game.amongUs.tasks;

import matanku.kakurembo.api.menu.Button;
import matanku.kakurembo.api.util.ItemBuilder;
import matanku.kakurembo.game.amongUs.GameAmongUsTask;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class OtwoOchibaTask extends GameAmongUsTask {
    private final Map<Integer, Button> gomi = new HashMap<>();

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int[] a = new int[]{0,1,2,9,10,11,18,19,20,27,28,29,36,37,38,45,46,47};
        for (int i : a) {
            buttons.put(i,new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).build();
                }
            });
        }

        List<Material> materials = Arrays.asList(Material.SHORT_GRASS, Material.OAK_SAPLING, Material.LARGE_FERN, Material.WHEAT_SEEDS, Material.SPRUCE_LEAVES);
        List<Integer> availableSlots = new ArrayList<>();
        for (int i = 0; i <= 53; i++) {
            final int finalI = i;
            if (Arrays.stream(a).noneMatch(n -> n == finalI)) {
                availableSlots.add(i);
            }
        }
        Collections.shuffle(availableSlots, new Random());
        for (Material material : materials) {
            for (int i = 0; i < 3; i++) {
                int slot = availableSlots.removeFirst();
                gomi.put(slot, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(material).build();
                    }

                    @Override
                    public void clicked(Player player, ClickType clickType) {
                        gomi.remove(slot);
                        openMenu(player);
                    }
                });
            }
        }

        buttons.putAll(gomi);

        return buttons;
    }
}
