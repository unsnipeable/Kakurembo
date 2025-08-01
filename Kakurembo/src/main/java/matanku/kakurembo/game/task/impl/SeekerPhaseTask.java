package matanku.kakurembo.game.task.impl;

import lombok.Getter;
import lombok.Setter;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.api.util.Symbols;
import matanku.kakurembo.enums.GameRole;
import matanku.kakurembo.game.task.GameTask;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.util.Util;
import matanku.kakurembo.api.util.Common;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class SeekerPhaseTask extends GameTask {
    public SeekerPhaseTask(int seconds) {
        super(seconds);
    }

    @Getter
    @Setter
    public static boolean tracker = false;

    @Override
    public void onRun() {
        if (tick == 0) {
            tracker = false;
            cancel();
            game.end();
        }
        if (tick == 60) {
            for (Map.Entry<UUID, GamePlayer> entry : game.getPlayers().entrySet()) {
                if (entry.getValue().getRole() == GameRole.HIDER) {
                    entry.getValue().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,Integer.MAX_VALUE,255,true,false));
                    Common.broadcastMessage("<gray>[<red>!<gray>] 残り1分になったためすべてのハイダーが発光しました!");
                }
            }
        }
        if (tick == 180) {
            if (game.getSettings().isTrackerEnabled()) {
                Common.broadcastMessage("<gray>[<red>!<gray>] 残り3分になったためシーカーに一番近くのハイダーとの距離が知らされました!");
                tracker = true;
            }
        }
        if (Util.ANNOUNCE.contains(tick)) {
            Common.broadcastSound(Sound.UI_BUTTON_CLICK);
        }

        game.getBossBar().name("<yellow>ハイダーは<aqua>" + Util.getTime(tick) + "<yellow>後に勝利します!").color(BossBar.Color.YELLOW).progress((float) tick / seconds);
        game.getBossBar().show();

        String players = game.getPlayers().values().stream().map(gp -> (gp.getRole() == GameRole.SEEKER ? "<red>" : "<blue>") + "<bold>" + gp.getPlayer().getName() + "<reset><gray>").collect(Collectors.joining(", "));
        game.getGamePlayersBossBar().name(players);
        game.getGamePlayersBossBar().show();


        for (Map.Entry<UUID,GamePlayer> entry : game.getPlayers().entrySet()) {
            if (entry.getValue().getStanCooldown() > 0) {
                entry.getValue().setStanCooldown(entry.getValue().getStanCooldown() - 1);
            }
            setProgressXP(entry.getValue().getPlayer(), entry.getValue().getStanCooldown());

            if (entry.getValue().getRole() == GameRole.SEEKER) {
                if (tracker) {
                    Map<GamePlayer, Double> trackerMap = new HashMap<>();
                    for (GamePlayer hider : game.getPlayers().values()) {
                        if (hider.getRole() == GameRole.HIDER) {
                            trackerMap.put(hider, hider.getPlayer().getLocation().distance(entry.getValue().getPlayer().getLocation()));
                        }
                    }
                    Map.Entry<GamePlayer, Double> minEntry = trackerMap.entrySet().stream().min(Map.Entry.comparingByValue()).orElse(null);
                    if (!(minEntry == null)) {
                        entry.getValue().getPlayer().sendActionBar(Common.text("<white>Tracking: <aqua>" + minEntry.getKey().getPlayer().getName() + " <white>- Distance: <green><bold>" + (int)Math.ceil(minEntry.getValue()) + "m <reset><white>Health: <red><bold>" + (int)(Math.ceil(minEntry.getKey().getPlayer().getHealth()*10)/10) + Symbols.HEALTH));
                    }
                }

                if (game.getSettings().isHeartBeatEnabled()) {
                    for (GamePlayer hider : game.getPlayers().values()) {
                        if (hider.getRole() == GameRole.HIDER) {
                            double distance = entry.getValue().getPlayer().getLocation().distance(hider.getPlayer().getLocation());
                            HideAndSeek.getINSTANCE().getLogger().info("WOOO " +distance);
                            if (distance <= 8) {
                                if (!(distance <= 4)) {
                                    entry.getValue().getPlayer().playSound(entry.getValue().getPlayer().getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 0.8f, 0.6f);
                                    HideAndSeek.getINSTANCE().getLogger().info("8block inai");
                                } else {
                                    new BukkitRunnable() {
                                        int count = 0;

                                        @Override
                                        public void run() {
                                            if (count >= 2) {
                                                this.cancel();
                                                return;
                                            }
                                            HideAndSeek.getINSTANCE().getLogger().info("4block inai");
                                            entry.getValue().getPlayer().playSound(entry.getValue().getPlayer().getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 0.8f, 1.4f);
                                            count++;
                                        }
                                    }.runTaskTimer(HideAndSeek.getINSTANCE(), 0L, 10L);
                                }
                            }
                        }
                    }
                }
            }
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
