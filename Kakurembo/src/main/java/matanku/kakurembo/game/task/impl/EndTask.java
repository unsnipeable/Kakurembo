package matanku.kakurembo.game.task.impl;

import matanku.kakurembo.config.datamanager.Manager;
import matanku.kakurembo.enums.DataEnum;
import matanku.kakurembo.enums.GameRole;
import matanku.kakurembo.game.task.GameTask;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.api.util.Common;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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

    @Override
    public void preRun() {

        Common.broadcastMessage(
                "",
                "<yellow>ゲームオーバー!",
                winner.getColoredName() + "<green>が勝利しました!",
                winner.getColoredName() + "<yellow>のメンバー: <aqua>" + game.getPlayers().values().stream().filter(gp -> gp.getRole() == winner).map(gp -> gp.getPlayer().getName()).collect(Collectors.joining("<gray>, <aqua>"))
        );
        List<GamePlayer> top3 = game.getPlayers().values().stream().sorted(Comparator.comparingInt(GamePlayer::getTrollPoint).reversed()).limit(3).toList();
        Common.broadcastMessage(
                "", "<gold>««　<yellow>トロールポイントランキング　<gold>»»", "<white>|  <gold>1位: <gold>" + top3.get(0).getPlayer().getName() + " (" + (top3.get(0).getTrollPoint() == 0 ? "なし" : top3.get(0).getTrollPoint()) + ")", (top3.size() > 1 ? "<white>|  <gray>2位: <gold>" + top3.get(1).getPlayer().getName() + " (" + (top3.get(1).getTrollPoint() == 0 ? "なし" : top3.get(1).getTrollPoint()) + ")" : "<white>|  <light_gray>2位: なし"), (top3.size() > 2 ? "<white>|  <dark_red>3位: <gold>" + top3.get(2).getPlayer().getName() + " (" + (top3.get(2).getTrollPoint() == 0 ? "なし" : top3.get(2).getTrollPoint()) + ")" : "<white>|  <dark_red>3位: なし"), ""
        );


        game.getBossBar().name("<green><bold>GAME OVER! <!bold>" + winner.getColoredName() + "<green>の勝利!").color(BossBar.Color.GREEN).progress(1);
        game.getBossBar().show();


        game.setLoaded(false);

        for (GamePlayer gamePlayer : game.getPlayers().values()) {
            gamePlayer.setDisguises(null);
            gamePlayer.setFlagged(0);


            Objects.requireNonNull(Manager.getDataManager("TrollDataManager")).addPlayerInfoInteger(gamePlayer.getUniqueID().toString(), gamePlayer.getTrollPoint(), DataEnum.DataManagerType.ADD);
            gamePlayer.setTrollPoint(0);
        }


        for (World w : Bukkit.getWorlds()) {
            for (Entity entity : w.getEntities()) {
                if (entity instanceof BlockDisplay) {
                    entity.remove();
                }
            }
        }

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
