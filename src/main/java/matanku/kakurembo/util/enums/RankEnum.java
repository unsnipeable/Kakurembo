package matanku.kakurembo.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RankEnum {
    NON(0, "<gray>", "<gray>NONE"),
    VIP(1,"<green>[VIP] ", "<green>VIP"),
    MVP(2,"<aqua>[MVP] ", "<aqua>MVP"),
    ADMIN(3, "<red>[ADMIN] ", "<red>ADMIN");

    private final int weight;
    private final String prefix;
    private final String displayName;
}
