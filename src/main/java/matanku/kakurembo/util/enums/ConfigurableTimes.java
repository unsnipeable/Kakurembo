package matanku.kakurembo.util.enums;

public enum ConfigurableTimes {
    HIDING("hider_time", "隠れる時間","ハイダーが隠れ場所に行くための時間"),
    SEEKER("game_time","探す時間","ゲームの時間"),
    TRACKER("tracker_time","トラッカー","トラッカーが使用できるようになる、ゲームの残り時間", true),
    GLOWING("glowing_time","発光","発光する、ゲームの残り時間時間", true),
    STUN("stun_cooldown","スタンのクールダウン","スタンが再度使用できるようになるクールダウン");


    public final String id;
    public final String displayName;
    public final String desc;
    public final boolean nokori;

    ConfigurableTimes(String id, String displayName, String desc) {
        this.id = id;
        this.displayName = displayName;
        this.desc = desc;
        this.nokori = false;
    }

    ConfigurableTimes(String id, String displayName, String desc, boolean nokori) {
        this.id = id;
        this.displayName = displayName;
        this.desc = desc;
        this.nokori = nokori;
    }
}
