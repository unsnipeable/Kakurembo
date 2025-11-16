package matanku.kakurembo.command.server;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.command.ServerCommand;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.util.enums.RankEnum;
import org.bukkit.entity.Player;

public class Mute extends ServerCommand {
    public Mute() {
        super("mute");
        this.setPerm(RankEnum.ADMIN);
    }

    @Override
    public void run(Player player, String[] args, String command) {
        GamePlayer gp = HideAndSeek.Instance.getGame().getPlayers().values().stream()
                .filter(gamePlayer -> {
                    Player p = gamePlayer.getPlayer();
                    return p != null && p.getName().equalsIgnoreCase(args[0]);
                }).findFirst().orElse(null);
        if (gp == null) {
            Common.sendMessage(player, "<red>" + args[0] + "という名前のプレイヤーが見つかりませんでした。");
            return;
        }
        gp.setMuted(!gp.isMuted());
        Common.sendMessage(player, "<red>" + gp.getPlayer().getName() + "を" + (gp.isMuted()?"":"アン") + "ミュートしました。");

    }
}
