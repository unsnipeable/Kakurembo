package matanku.kakurembo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import matanku.kakurembo.Items;

@Getter
@RequiredArgsConstructor
public enum GameRole {

    NONE("未設定", "<gray>", null, new ItemStack[]{}),
    HIDER("ハイダー", "<aqua>", "制限時間内にシーカーに発見されない", new ItemStack[]{
            Items.TRANSFORM_TOOL.getItem(),
            Items.TELEPORT_TOOL.getItem(),
            Items.KNOCKBACK_STICK.getItem(),
            Items.STUN.getItem()
    }),
    SEEKER("シーカー", "<red>", "制限時間内にハイダー全員を見つける", new ItemStack[]{
            Items.NETHERITE_SWORD.getItem()
    });

    private final String name;
    private final String color;
    private final String goal;
    private final ItemStack[] tools;

    @Override
    public String toString() {
        return name;
    }

    public String getColoredName() {
        return color + name;
    }
}
