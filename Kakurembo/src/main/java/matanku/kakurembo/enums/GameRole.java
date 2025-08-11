package matanku.kakurembo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.api.util.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import matanku.kakurembo.Items;

@Getter
@RequiredArgsConstructor
public enum GameRole {

    NONE("未設定", "未設定", "<gray>", null, null, new ItemStack[]{}),
    HIDER("ハイダー", "クルー","<aqua>", "制限時間内にシーカーに発見されない", "インポスターから隠れたり逃げ、全てのタスクを行う、又はインポスターを投票で追い出す", new ItemStack[]{
            Items.TRANSFORM_TOOL.getItem(),
            Items.TELEPORT_TOOL.getItem(),
            Items.KNOCKBACK_STICK.getItem(),
            Items.STUN.getItem()
    }),
    SEEKER("シーカー", "インポスター", "<red>", "制限時間内にハイダー全員を見つける","クルーが残り1人になるまで倒す", new ItemStack[]{
            null
    });

    private final String name;
    private final String amongUs;
    private final String color;
    private final String goal;
    private final String amongUsGoal;
    private final ItemStack[] tools;

    @Override
    public String toString() {
        return name;
    }

    public String getColoredName() {
        return color + name;
    }
    public String getAmongUsName() {
        return color + amongUs;
    }
}
