package matanku.kakurembo.game.amongUs.tasks;

import matanku.kakurembo.api.menu.Button;
import matanku.kakurembo.api.util.ItemBuilder;
import matanku.kakurembo.game.amongUs.GameAmongUsTask;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CafeteriaOchibaTask extends GameAmongUsTask {
    private int step = 0;
    private Map<Integer, Button> gomi = new HashMap<>();
    private Map<Integer, Button> firstGomi = new HashMap<>();

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for (int i : new int[]{0,1,2,3,4,5,6,7,8,9,15,17,18,24,26,27,33,35,36,42,44,45,51,52,53}) {
            buttons.put(i,new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).build();
                }
            });
        }

        if (step == 0) {
            List<Material> materials = Arrays.asList(Material.SHORT_GRASS,Material.OAK_SAPLING,Material.LARGE_FERN,Material.WHEAT_SEEDS,Material.SPRUCE_LEAVES);
            List<Integer> availableSlots = new ArrayList<>();
            for (int slot : new int[]{10,11,12,13,14,20,21,22,23,24,30,31,32,33,34,40,41,42,43,44}) {
                availableSlots.add(slot);
            }
            Collections.shuffle(availableSlots, new Random());
            for (Material material : materials) {
                for (int i = 0; i < 2; i++) {
                    int slot = availableSlots.removeFirst();
                    gomi.put(slot, new Button() {
                        @Override
                        public ItemStack getButtonItem(Player player) {
                            return new ItemBuilder(material).build();
                        }
                    });
                }
            }
            firstGomi = new HashMap<>(gomi);
        } else {
            for (Map.Entry<Integer,Button> entry : firstGomi.entrySet()) {
                int newSlot = entry.getKey() + 10 * step;
                if (!(newSlot >= 50)) {
                    gomi.put(newSlot, entry.getValue());
                }
            }
        }

        buttons.putAll(gomi);

        buttons.put(16 + step * 9,new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.LEVER).name("クリックして落ち葉を廃棄").build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                if (step == 3) {
                    clear(player);
                } else {
                    step++;
                }
            }
        });

        return buttons;
    }
}
