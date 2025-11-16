package matanku.kakurembo.util.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CheckPointStatus {
    NOTPLAYING(0),WHITE(1),RED(2),ORANGE(3),YELLOW(4),LIME(5),LIGHT_BLUE(6),BLUE(7);

    public final int i;
    public CheckPointStatus next() {
        return CheckPointStatus.values()[(this.ordinal() + 1) % CheckPointStatus.values().length];
    }
}
