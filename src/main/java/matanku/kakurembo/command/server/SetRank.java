package matanku.kakurembo.command.server;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.command.ServerCommand;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.util.Checker;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.util.enums.RankEnum;
import org.bukkit.entity.Player;

public class SetRank extends ServerCommand {
    public SetRank() {
        super("rank");
        this.setPerm(RankEnum.ADMIN);
        this.setAliases(new String[]{"setrank"});
    }

    @Override
    public void run(Player player, String[] args, String command) {
        if (args == null) {
            Common.sendMessage(player, ("<red>/%s <player> <rank>").formatted(command));
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
            boolean changed = false;
            if (Checker.isInteger(args[1])) {
                gp.setRank(Integer.parseInt(args[1]));
                changed = true;
            } else if (Checker.isRank(args[1])) {
                gp.setRank(RankEnum.valueOf(args[1]));
                changed = true;
            } else {
                Common.sendMessage(player, "<red>" + args[1] + "という名前/優先度のランクを見つけられませんでした。");
            }
            if (changed) Common.sendMessage(gp.getPlayer(), "<green>あなたのランクが " + gamePlayer(player).getDisplayName() + "<green> によって " + gp.getRank().getDisplayName() + " <green>に変更されました!");
        } else {
            Common.sendMessage(player, ("<red>/%s " + args[0] + " <rank>").formatted(command));
        }
    }
}
