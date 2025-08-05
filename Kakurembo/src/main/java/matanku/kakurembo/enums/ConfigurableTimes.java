package matanku.kakurembo.enums;

public enum ConfigurableTimes {
    HIDING("hider_time", "隠れる時間","ハイダーが隠れ場所に行くための時間"),
    SEEKER("game_time","探す時間","ゲームの時間"),
    TRACKER("tracker_time","トラッカー","トラッカーが使用できるようになる、ゲームの残り時間"),
    GLOWING("glowing_time","発光","発光する、ゲームの残り時間時間"),
    STUN("stun_cooldown","スタンのクールダウン","スタンが再度使用できるようになるクールダウン");


    public final String id;
    public final String displayName;
    public final String desc;

    ConfigurableTimes(String id, String displayName, String desc) {
        this.id = id;
        this.displayName = displayName;
        this.desc = desc;
    }
}
