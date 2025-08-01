package matanku.kakurembo.game.task.impl;

import matanku.kakurembo.enums.GameRole;
import matanku.kakurembo.game.task.GameTask;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.util.Util;
import matanku.kakurembo.api.util.Common;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class SeekerPhaseTask extends GameTask {
    public SeekerPhaseTask(int seconds) {
        super(seconds);
    }

    @Override
    public void onRun() {
        if (tick == 0) {
            cancel();
            game.end();
        }
        if (tick == 60) {
            for (Map.Entry<UUID, GamePlayer> entry : game.getPlayers().entrySet()) {
                if (entry.getValue().getRole() == GameRole.HIDER) {
                    entry.getValue().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,Integer.MAX_VALUE,255,true,false));
                    Common.broadcastMessage("<gray>[<red>!<gray>] すべてのハイダーが発光しました!");
                }
            }
        }
        if (Util.ANNOUNCE.contains(tick)) {
            Common.broadcastSound(Sound.UI_BUTTON_CLICK);
        }

        game.getBossBar().name("<yellow>ハイダーは<aqua>" + Util.getTime(tick) + "<yellow>後に勝利します!").color(BossBar.Color.YELLOW).progress((float) tick / seconds);
        game.getBossBar().show();

        String players = game.getPlayers().values().stream().map(gp -> (gp.getRole() == GameRole.HIDER ? "<red>" : "<blue>") + "<bold>" + gp.getPlayer() + "<reset><gray>").collect(Collectors.joining(", "));
        game.getGamePlayersBossBar().name(players);
        game.getGamePlayersBossBar().show();


        for (Map.Entry<UUID,GamePlayer> entry : game.getPlayers().entrySet()) {
            if (entry.getValue().getStanCooldown() > 0) {
                entry.getValue().setStanCooldown(entry.getValue().getStanCooldown() - 1);
            }
            setProgressXP(entry.getValue().getPlayer(), entry.getValue().getStanCooldown());
        }
    }

    public void setProgressXP(Player player, int ticks) {
        float progress = (float) Math.min(1.0, ticks / 60.0);
        player.setLevel(ticks);
        player.setExp(progress);
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
