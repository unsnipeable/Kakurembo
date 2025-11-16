package matanku.kakurembo.command.server;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.command.ServerCommand;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.util.Checker;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.util.enums.RankEnum;
import org.bukkit.entity.Player;

public class SetStar extends ServerCommand {
    public SetStar() {
        super("star");
        this.setPerm(RankEnum.ADMIN);
        this.setAliases(new String[]{"setstar", "setlevel"});
    }

    @Override
    public void run(Player player, String[] args, String command) {
        if (args == null) {
            Common.sendMessage(player, ("<red>/%s <player> <star>").formatted(command));
            return;
        }
        GamePlayer gp = HideAndSeek.Instance.getGame().getPlayers().values().stream()
                .filter(gamePlayer -> {
                    Player p = gamePlayer.getPlayer();
                    return p != null && p.getName().equalsIgnoreCase(args[0]);
                }).findFirst().orElse(null);
        if (gp == null) {
            Common.sendMessage(player, "<red>" + args[0] + "という名前のプレイヤーが見つかりませんでした。");
            return;
        }
        if (args.length == 2) {
            if (Checker.isInteger(args[1])) {
                gp.setStar(Integer.parseInt(args[1]));
                Common.sendMessage(gp.getPlayer(), "<green>あなたのレベルが " + gamePlayer(player).getDisplayName() + "<green> によって " + gp.getPrestigeFormatWithoutBracket() + " <green>に変更されました!");
            } else {
                Common.sendMessage(player, "<red>入力された値" + args[1] + " は数値ではありません!");
            }
        } else {
            Common.sendMessage(player, ("<red>/%s " + args[0] + " <star>").formatted(command));
        }
    }
}
