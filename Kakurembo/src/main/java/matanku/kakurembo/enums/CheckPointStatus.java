package matanku.kakurembo.enums;

public enum CheckPointStatus {
    NOTPLAYING,WHITE,RED,ORANGE,YELLOW,LIME,LIGHT_BLUE,BLUE;

    public CheckPointStatus next() {
        return CheckPointStatus.values()[(this.ordinal() + 1) % CheckPointStatus.values().length];
    }
}
