package matanku.kakurembo.player;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.game.Game;
import matanku.kakurembo.game.GameMap;
import matanku.kakurembo.game.task.impl.HiderPhaseTask;
import matanku.kakurembo.game.task.impl.SeekerPhaseTask;
import org.bukkit.*;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public class Replay {
    public Map<Integer, Map<String,ReplayData>> replay = new HashMap<>();
    public Map<Entity, ReplayData> entityData = new HashMap<>();

    public int time = 0;
    public boolean pause = false;

    public Location getPlayerLocationFromTick(int tick, String st) {
        Location loc = null;
        try {
            loc = replay.get(tick).get(st).loc();
        } catch (Exception ignored) {}
        return loc;
    }

    public void recode(Game game, int tick) {
        if (game.getCurrentTask() instanceof SeekerPhaseTask) {
            Map<String,ReplayData> tmp = new HashMap<>();
            for (GamePlayer gamePlayer : game.getPlayers().values()) {
                tmp.put(gamePlayer.getPlayer().getName(), new ReplayData(gamePlayer.getPlayer().getLocation(), Material.getMaterial(gamePlayer.getDisguises().getData()), game.getMap()));
            }
            replay.put(tick,tmp);
        }
    }


    public void generateWorld(GamePlayer gamePlayer) {
        gamePlayer.setParkour(false);
        GameMap world = replay.get(0).get(gamePlayer.getPlayer().getName()).world();
        world.generateMap("replay_" + gamePlayer.getPlayer().getName(), bool -> {
            if (bool) {
                world.teleport(gamePlayer.getPlayer());
                world.getWorld().setGameRule(GameRule.FALL_DAMAGE, false);
                gamePlayer.getPlayer().setGameMode(GameMode.ADVENTURE);
                gamePlayer.getPlayer().setAllowFlight(true);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!pause) {
                            for (Map.Entry<String, ReplayData> ent : replay.get(0).entrySet()) {
                                boolean tmp = false;
                                for (Map.Entry<Entity, ReplayData> ents : entityData.entrySet()) {
                                    if (ents.getKey().getName().equals(ent.getKey())) tmp = true;
                                }
                                if (!tmp) {
                                    Entity e = world.getWorld().spawnEntity(ent.getValue().loc(), EntityType.PLAYER);
                                    e.customName(Common.text(ent.getKey()));
                                }
                            }
                            for (Entity e : world.getWorld().getEntities()) {
                                if (e instanceof BlockDisplay) {
                                    e.remove();
                                }
                            }
                            for (Map.Entry<Entity, ReplayData> entry : entityData.entrySet()) {
                                if (getPlayerLocationFromTick(time, entry.getKey().getName()) == entry.getValue().loc()) {
                                    entry.getKey().teleport(getPlayerLocationFromTick(time, entry.getKey().getName()));
                                    world.getWorld().spawn(entry.getValue().loc(), BlockDisplay.class, display -> {
                                        display.setBlock(Bukkit.createBlockData(entry.getValue().disguise()));
                                        display.setTeleportDuration(1);
                                    });
                                }
                            }
                            time++;
                        }
                    }
                }.runTaskTimer(HideAndSeek.getINSTANCE(),0L,1L);
            } else {
                Common.broadcastMessage("<red>マップ生成中にエラーが発生しました。");
            }
        });
    }
}
