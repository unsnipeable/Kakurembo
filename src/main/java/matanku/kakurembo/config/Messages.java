package matanku.kakurembo.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import matanku.kakurembo.game.enums.GameRole;
import matanku.kakurembo.player.GamePlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Messages {
    @Getter
    @RequiredArgsConstructor
    public enum Message {
        DEFAULT("デフォルト",
                "%player%は%attacker%に倒され、%seeker%になりました!",
                true, 0, new ItemStack(Material.BARRIER),1),

        KING("キング",
                "%player%による反乱は%attacker%によって阻止された。",
                false, 100, new ItemStack(Material.GOLDEN_HELMET),2),

        ONEVONE("1v1",
                "%player%は%attacker%との1v1に負けた。",
                false, 200, new ItemStack(Material.IRON_SWORD),3),

        BEEF("焼肉",
                "%player%は%attacker%によって焼かれた。",
                false, 200, new ItemStack(Material.COOKED_BEEF),4),

        KASAIRYUU("火砕流",
                "%player%は%attacker%の管理する火山の火砕流に巻き込まれた。",
                false, 200, new ItemStack(Material.LAVA_BUCKET),5),

        TROUBLEMAKER("クレーマー",
                "%player%の不満は%attacker%によって論破された。",
                false, 200, new ItemStack(Material.COMPASS),6),

        BEDWARS1("ベッドウォーズ ベッド破壊",
                "<white><bold>BED DESTRUCTION > <!bold>%player%のベッドは%attacker%によって破壊された。",
                false, 500, new ItemStack(Material.RED_BED),7),

        BEDWARS2("ベッドウォーズ ファイナルキル",
                "%player%は%attacker%に殺された。<aqua><bold>FINAL KILL!<!bold>",
                false, 500, new ItemStack(Material.RED_BED),8),

        BUSU("ブス",
                "%player%は%seeker%にブス判定された。",
                false, 5000, new ItemStack(Material.DIRT),9),

        DEBUG("デバッグ",
                     "<gray>[<red>M<gold>y<yellow>a<green>u<gray>] %player%<white>: %seeker%",
                     false, Integer.MAX_VALUE, new ItemStack(Material.DEBUG_STICK),10);

        private final String title;
        private final String message;
        private final boolean unlockedAsDefault;
        private final int unlockCoin;
        private final ItemStack icon;
        private final int i;

    }

    public static String getDeathMessage(GamePlayer attacker, String loser) {
        String selected = attacker.getSelectedKillMessage().getMessage();
        Map<String, String> replaces = new HashMap<>() {{
            put("player","<gray>" + loser);
            put("attacker","<gray>" + attacker.getPlayer().getName());
            put("seeker", GameRole.SEEKER.getColoredName() + "<gray>");
            put("hider", GameRole.HIDER.getColoredName() + "<gray>");
        }};
        for (Map.Entry<String, String> entry : replaces.entrySet()) {
            selected = selected.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return selected;
    }

    public static Messages.Message getMessageFromI(int i) {
        for (Message m : Message.values()) {
            if (m.i == i) return m;
        }
        return null;
    }

}
