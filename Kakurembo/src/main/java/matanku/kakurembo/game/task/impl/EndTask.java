package matanku.kakurembo.game.task.impl;

import matanku.kakurembo.enums.GameRole;
import matanku.kakurembo.game.task.GameTask;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.api.util.Common;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;

import java.util.stream.Collectors;

public class EndTask extends GameTask {

    private final GameRole winner;

    public EndTask() {
        super(7);

        if (game.getPlayers().values().stream().noneMatch(gp -> gp.getRole() == GameRole.HIDER)) {
            winner = GameRole.SEEKER;
        } else {
            winner = GameRole.HIDER;
        }
    }

    private boolean isBroadcasted = false;

    @Override
    public void preRun() {

        if (isBroadcasted) return;
        Common.broadcastMessage(
                "",
                "<yellow>ゲームオーバー!",
                winner.getColoredName() + "<green>が勝利しました!",
                winner.getColoredName() + "<yellow>そのチームのメンバー: <aqua>" + game.getPlayers().values().stream().filter(gp -> gp.getRole() == winner).map(gp -> gp.getPlayer().getName()).collect(Collectors.joining("<gray>, <aqua>")),
                ""
        );


        game.getBossBar().name("<green><bold>GAME OVER! <!bold>" + winner.getColoredName() + "<green>の勝利!").color(BossBar.Color.GREEN).progress(1);
        game.getBossBar().show();

        game.getGamePlayersBossBar().destroy();
        for (GamePlayer gamePlayer : game.getPlayers().values()) {
            gamePlayer.setDisguises(null);
            gamePlayer.setFlagged(0);
        }


        for (World w : Bukkit.getWorlds()) {
            for (Entity entity : w.getEntities()) {
                if (entity instanceof BlockDisplay) {
                    entity.remove();
                }
            }
        }

        isBroadcasted = true;
    }

    @Override
    public void onRun() {
        if (tick == 0) {
            cancel();
            game.reset();
        }
    }

    @Override
    public TickType getTickType() {
        return TickType.COUNT_DOWN;
    }

    @Override
    public int getStartTick() {
        return seconds;
    }
}
