package matanku.kakurembo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import matanku.kakurembo.player.GamePlayer;

@Getter
@RequiredArgsConstructor
public enum Prestige {

    // bw pres
    DEFAULT(0, "&7[%star%✫]"),
    IRON(10, "&f[%star%✫]"),
    GOLD(20, "&6[%star%✫]"),
    DIAMOND(30, "&b[%star%✫]"),
    EMERALD(40, "&2[%star%✫]"),
    SAPPHIRE(50, "&3[%star%✫]"),
    RUBY(60, "&4[%star%✫]"),
    CRYSTAL(70, "&d[%star%✫]"),
    OPAL(80, "&9[%star%✫]"),
    AMETHYST(90, "&5[%star%✫]"),
    RAINBOW(100, "&c[&6%1%&e%2%&a%3%&b✫&d]"),

    //original
    SHADOW(110, "&7[&f%star%&7]"),
    SUN(120, "&4[&c%star%&4]")
    ;

    private final int star;

    private final String format;
}
