package matanku.kakurembo.game;

import lombok.Getter;
import lombok.Setter;
import matanku.kakurembo.config.Config;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.util.*;
import matanku.kakurembo.enums.DisguiseTypes;
import matanku.kakurembo.enums.GameRole;
import matanku.kakurembo.enums.GameState;
import matanku.kakurembo.game.disguise.DisguiseData;
import matanku.kakurembo.game.task.impl.*;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.util.GlobalBossBar;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@Getter
@Setter
public class Game {

    public static final String DISGUISE_KEY = "disguise";

    private final Map<UUID, GamePlayer> players = new HashMap<>();
    private boolean loaded = false;
    private GameMap map = new GameMap();
    private GameSettings settings = new GameSettings();
    private GameState state = GameState.WAITING;
    private GlobalBossBar bossBar;
    private GlobalBossBar gamePlayersBossBar;
    private TaskTicker currentTask;
    private int tick;

    public Game returnThis() {
        return this;
    }

    public void startCountdown(int seconds) {
        if (loaded) return;
        state = GameState.STARTING;
        bossBar = new GlobalBossBar(BossBar.bossBar(Common.text("<yellow>準備時間"), 1, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS), "game");
        gamePlayersBossBar = new GlobalBossBar(BossBar.bossBar(Common.text("<aqua>またんくかくれんぼ"),0,BossBar.Color.BLUE, BossBar.Overlay.PROGRESS), "nokori");
        currentTask = new CountdownPhaseTask(seconds);
        tick = 0;
        loaded = true;
    }

    //
    // Todo: map生成を単純に行う
    //
    public void generateWorld() {
        for (GamePlayer gp : getPlayers().values()) {
            gp.setParkour(false);
        }
        bossBar.name("<yellow>マップを生成しています").progress(1);

        map.generateMap("world_game", bool -> {
            if (bool) {
                startInstructionPhase(10);
            } else {
                Common.broadcastMessage("<red>マップ生成中にエラーが発生しました。");
            }
        });
    }

    public void startInstructionPhase(int seconds) {
        state = GameState.INSTRUCTION_PHASE;

        List<GamePlayer> gamePlayers = new ArrayList<>(players.values());
        Collections.shuffle(gamePlayers);
        for (GamePlayer gamePlayer : gamePlayers) {
            Player player = gamePlayer.getPlayer();
            player.setAllowFlight(false);


            if (gamePlayer.getRole() == GameRole.NONE) {
                if (players.values().stream().filter(gp -> gp.getRole() == GameRole.SEEKER).count() < (settings.getMaxSeekers() == -1 ? (gamePlayers.size() / 5) + 1 : settings.getMaxSeekers())) {
                    gamePlayer.setRole(GameRole.SEEKER);
                } else {
                    gamePlayer.setRole(GameRole.HIDER);
                }
            }

            Common.sendMessage(player, "", "<yellow>あなたは今" + gamePlayer.getRole().getColoredName() + "<yellow>です!");
            if (gamePlayer.getRole().getGoal() != null) {
                Common.sendMessage(player, "<yellow>勝利条件: <dark_aqua>" + gamePlayer.getRole().getGoal());
            }
            Common.sendMessage(player, "");
        }

        currentTask = new InstructionPhaseTask(seconds);
    }

    public void startHiderPhase(int seconds) {
        state = GameState.HIDING_PHASE;

        Common.broadcastMessage("","<yellow>ゲームが<green>開始<yellow>されました!",GameRole.HIDER.getColoredName() + "<yellow>は<aqua>" + seconds + "秒<yellow>以内に隠れないといけません!","");
        
        for (GamePlayer gamePlayer : players.values()) {
            Player player = gamePlayer.getPlayer();
            GameRole role = gamePlayer.getRole();

            if (getSettings().getSpeed() >= 1) player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, getSettings().getSpeed(),false,false));
            
            if (role == GameRole.HIDER) {
                map.teleport(player);
                gamePlayer.setDisguises(new DisguiseData(DisguiseTypes.BLOCK, "OAK_PLANKS"));
                gamePlayer.getDisguises().setDisguise(Util.disguise(player));
                player.getInventory().setContents(role.getTools());
            }
        }

        currentTask = new HiderPhaseTask(seconds);
    }

    public void startSeekerPhase(int seconds) {
        state = GameState.SEEKER_PHASE;

        Common.broadcastMessage("", GameRole.SEEKER.getColoredName() + "<yellow>が解放されました!", "<yellow>もし" + GameRole.SEEKER.getColoredName() + "<yellow>が<aqua>" + (seconds >= 60 ? (seconds / 60) + "分" : seconds + "秒") + "<yellow>以内にすべての" + GameRole.HIDER.getColoredName() + "<yellow>を見つけることができなかった場合," + GameRole.SEEKER.getColoredName() + "<yellow>が勝利します!", "<yellow>皆さんの幸運を祈る!", "");

        for (GamePlayer gamePlayer : players.values()) {
            Player player = gamePlayer.getPlayer();
            GameRole role = gamePlayer.getRole();

            if (role == GameRole.SEEKER) {
                map.teleport(player);
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                player.getInventory().addItem(new ItemBuilder(HideAndSeek.getInstance().getGame().getSettings().getSwordType().getItem()).name(HideAndSeek.getInstance().getGame().getSettings().getSwordType().getName()).unbreakable().enchantmentBoolean(Enchantment.FIRE_ASPECT, 1, HideAndSeek.getInstance().getGame().getSettings().isSwordFire()).build(true));
            }
        }
        currentTask = new SeekerPhaseTask(seconds);
        SeekerPhaseTask.setTracker(false);
    }

    public void end() throws AssertionError {
        assert isStarted() : "プレイ状態ではありません!";

        state = GameState.ENDING;

        currentTask.cancel();
        currentTask = new EndTask();

        for (Map.Entry<UUID, GamePlayer> entry : getPlayers().entrySet()) {
            if (entry.getValue().getDisguises() == null) continue;
            entry.getValue().getDisguises().getDisguise().stopDisguise();
            entry.getValue().setTrollPoint(0);
        }
    }

    public void reset() {
        players.values().forEach(gp -> {
            gp.reset();
            PlayerUtil.reset(gp.getPlayer());
            gp.getPlayer().teleport(Config.LOBBY_LOCATION);
        });
        map = new GameMap();

        state = GameState.WAITING;
        if (gamePlayersBossBar != null) {
            gamePlayersBossBar.destroy();
            gamePlayersBossBar = null;
        }
        if (bossBar != null) {
            bossBar.destroy();
            bossBar = null;
        }
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }
    }

    public boolean isStarted() {
        return state == GameState.INSTRUCTION_PHASE || state == GameState.HIDING_PHASE || state == GameState.SEEKER_PHASE || state == GameState.ENDING;
    }

    public boolean canEnd() {
        return players.values().stream().noneMatch(gp -> gp.getRole() == GameRole.HIDER) || players.values().stream().noneMatch(gp -> gp.getRole() == GameRole.SEEKER);
    }

    public GamePlayer getGamePlayer(Player player) {
        return getGamePlayer(player.getUniqueId());
    }

    public GamePlayer getGamePlayer(UUID uuid) {
        return players.get(uuid);
    }

}
