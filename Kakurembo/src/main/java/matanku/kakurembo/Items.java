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
    SWORD(new ItemBuilder(Material.NETHERITE_SWORD).unbreakable().build()),
    STUN(new ItemBuilder(Material.TRIPWIRE_HOOK).name("<yellow>スタン").lore("<gray>殴ったシーカーに移動速度低下と盲目を付与します。", "クールダウン: 60秒").build()),

    PARKOUR_CHECKPOINT(new ItemBuilder(Material.HEAVY_WEIGHTED_PRESSURE_PLATE).name("<green>最後のチェックポイントにテレポート").build()),
    PARKOUR_SPAWN(new ItemBuilder(Material.OAK_DOOR).name("<yellow>スポーンに戻る").build()),
    PARKOUR_CANCEL(new ItemBuilder(Material.RED_BED).name("<red>パルクールをやめる").build()),
    ;

    private final ItemStack item;

    public ItemStack getItem() {
        if (this == Items.SWORD) {
            return new ItemBuilder(HideAndSeek.getINSTANCE().getGame().getSettings().getSwordType().getItem()).name(HideAndSeek.getINSTANCE().getGame().getSettings().getSwordType().getName()).unbreakable().build().clone();
        }
        return item.clone();
    }
}
