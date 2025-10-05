package matanku.kakurembo.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public enum SwordType {

    WOOD("木の剣", new ItemStack(Material.WOODEN_SWORD)),
    STONE("石の剣", new ItemStack(Material.STONE_SWORD)),
    IRON("鉄の剣", new ItemStack(Material.IRON_SWORD)),
    GOLD("金の剣", new ItemStack(Material.GOLDEN_SWORD)),
    DIAMOND("ダイヤの剣", new ItemStack(Material.DIAMOND_SWORD)),
    NETHERITE("ネザライトの剣", new ItemStack(Material.NETHERITE_SWORD)),
    STICK("鈍器", new ItemStack(Material.STICK));

    private final String name;
    private final ItemStack item;

    public SwordType next() {
        return SwordType.values()[(this.ordinal() + 1) % SwordType.values().length];
    }
}
