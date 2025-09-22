package matanku.kakurembo.game.task.impl;

import lombok.Getter;
import lombok.Setter;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.api.util.Symbols;
import matanku.kakurembo.config.datamanager.Manager;
import matanku.kakurembo.enums.DataEnum;
import matanku.kakurembo.enums.GameRole;
import matanku.kakurembo.game.task.GameTask;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.util.Util;
import matanku.kakurembo.api.util.Common;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Location;
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

    public static boolean removeRunnable = false;

    @Override
    public void onRun() {
        if (tick == 0) {
            tracker = false;
            cancel();
            game.end();
        }

        if (tick % 60 == 0) {
            for (GamePlayer gamePlayer : game.getPlayers().values()) {
                Objects.requireNonNull(Manager.getDataManager("CoinDataManager")).addPlayerInfoInteger(gamePlayer.getPlayer().getUniqueId().toString(), 10, DataEnum.DataManagerType.ADD);
                Common.sendMessage(gamePlayer.getPlayer(), "<gold>+10コイン! (プレイ時間)");
            }
        }

        if (tick == game.getSettings().getTimes().getOrDefault("glowing_time", 180)) {
            for (Map.Entry<UUID, GamePlayer> entry : game.getPlayers().entrySet()) {
                if (entry.getValue().getRole() == GameRole.HIDER) {
                    entry.getValue().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 255, true, false));
                    Common.broadcastMessage("<gray>[<red>!<gray>] 残り1分になったためすべてのハイダーが発光しました!");
                }
            }
        }
        if (tick == game.getSettings().getTimes().getOrDefault("tracker_time", 180)) {
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


        for (Map.Entry<UUID, GamePlayer> entry : game.getPlayers().entrySet()) {
            if (entry.getValue().getGlowingHintCooldown() > 0) {
                entry.getValue().setGlowingHintCooldown(entry.getValue().getGlowingHintCooldown()-1);
            } else if (entry.getValue().getGlowingHintCooldown() == 0) {
                Common.sendMessage(entry.getValue().getPlayer(), "<green>あなたの発光ヒントのクールダウンが終了しました!");
                entry.getValue().setGlowingHintCooldown(-1);
            }

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
                        entry.getValue().getPlayer().sendActionBar(Common.text("<white>Tracking: <aqua>" + minEntry.getKey().getPlayer().getName() + " <white>- Distance: <green><bold>" + (int) Math.ceil(minEntry.getValue()) + "m <reset><white>Direction: <light_purple><bold> "+ getDirection(entry.getValue().getPlayer(),minEntry.getKey().getPlayer()) +"<reset><white>Health: <red><bold>" + (int) (Math.ceil(minEntry.getKey().getPlayer().getHealth() * 10) / 10) + Symbols.HEALTH));
                    }
                }

                if (game.getSettings().isHeartBeatEnabled()) {
                    for (GamePlayer hider : game.getPlayers().values()) {
                        if (hider.getRole() == GameRole.HIDER) {
                            double distance = entry.getValue().getPlayer().getLocation().distance(hider.getPlayer().getLocation());
                            if (distance <= 8) {
                                if (!(distance <= 3)) {
                                    entry.getValue().getPlayer().playSound(entry.getValue().getPlayer().getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1f, 0.8f);
                                } else {
                                    new BukkitRunnable() {
                                        int count = 0;

                                        @Override
                                        public void run() {
                                            if (removeRunnable) {
                                                this.cancel();
                                            }
                                            if (count >= 2) {
                                                this.cancel();
                                                return;
                                            }
                                            entry.getValue().getPlayer().playSound(entry.getValue().getPlayer().getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1f, 1.4f);
                                            count++;
                                        }
                                    }.runTaskTimer(HideAndSeek.getInstance(), 0L, 10L);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setProgressXP(Player player, int ticks) {
        float progress = (float) Math.min(1.0, ticks / ((double)game.getSettings().getTimes().get("stun_cooldown")));
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
    public String getDirection(Player player1, Player player2) {
        Location loc1 = player1.getLocation();
        Location loc2 = player2.getLocation();

        double dx = loc2.getX() - loc1.getX();
        double dz = loc2.getZ() - loc1.getZ();

        float playerYaw = loc1.getYaw();
        float relativeYaw = (float) Math.toDegrees(Math.atan2(-dx, dz)) - playerYaw;
        relativeYaw = (relativeYaw + 360) % 360;
        String direction = "";
        if (relativeYaw >= 45 && relativeYaw < 135) {
            direction += "←";
        } else if (relativeYaw >= 135 && relativeYaw < 225) {
            direction += "↓";
        } else if (relativeYaw >= 225 && relativeYaw < 315) {
            direction += "→";
        } else {
            direction += "↑";
        }

        return direction;
    }


}
