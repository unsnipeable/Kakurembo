package matanku.kakurembo;

import lombok.RequiredArgsConstructor;
import matanku.kakurembo.api.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public enum Items {

    TRANSFORM_TOOL(new ItemBuilder(Material.REDSTONE).name("<yellow>変身").lore("<gray>触ったブロックに変身").build()),
    TELEPORT_TOOL(new ItemBuilder(Material.CLOCK).name("<yellow>固定").lore("<gray>現在の場所にブロックを固定します。", "<gray>注意: 他のプレイヤーは変装を通り抜けることができます").build()),
    KNOCKBACK_STICK(new ItemBuilder(Material.STICK).name("<yellow>鈍器").lore("<gray>近くのプレイヤーをノックバックします。").enchantment(Enchantment.KNOCKBACK, 1).build()),
    NETHERITE_SWORD(new ItemBuilder(Material.NETHERITE_SWORD).unbreakable().build()),
    STUN(new ItemBuilder(Material.TRIPWIRE_HOOK).name("<yellow>スタン").lore("<gray>殴ったシーカーに移動速度低下と盲目を付与します。", "クールダウン: 60秒").build()),
    ;

    private final ItemStack item;

    public ItemStack getItem() {
        return item.clone();
    }
}
