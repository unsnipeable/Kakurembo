package matanku.kakurembo.game;

import io.papermc.paper.event.player.AsyncChatEvent;
import matanku.kakurembo.config.Config;
import matanku.kakurembo.enums.Items;
import matanku.kakurembo.util.ItemBuilder;
import matanku.kakurembo.config.Messages;
import matanku.kakurembo.config.datamanager.DataManager;
import matanku.kakurembo.config.datamanager.Manager;
import matanku.kakurembo.config.datamanager.impl.ParkourDataManager;
import matanku.kakurembo.enums.*;
import matanku.kakurembo.game.task.impl.SeekerPhaseTask;
import matanku.kakurembo.menu.cosmetic.CosmeticMenu;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.util.PlayerUtil;
import matanku.kakurembo.menu.Button;
import matanku.kakurembo.menu.Menu;
import matanku.kakurembo.menu.Registers;
import matanku.kakurembo.menu.button.IntegerButton;
import matanku.kakurembo.menu.button.ToggleButton;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.util.Util;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.event.GamePlayerDeathEvent;

import java.util.concurrent.ThreadLocalRandom;

import java.util.*;

public class GameListener implements Listener {

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        Game game = HideAndSeek.Instance.getGame();

        if (Config.WHITELIST) {
            if (Bukkit.getOfflinePlayer(event.getUniqueId()).isOp()) return;
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Common.text("<red>現在メンテナンス中です。"));
        }
        if (game.isStarted()) {
            if (!game.getSettings().isAllowJoinAsSeekerAfterStarted()) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Common.text("<red>管理者が途中参加設定をオフにしているため、ゲームの途中にログインできません。"));
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Game game = HideAndSeek.Instance.getGame();

        game.getPlayers().putIfAbsent(player.getUniqueId(), new GamePlayer(player.getUniqueId()));

        String playerKey = player.getUniqueId().toString();

        for (DataManager dm : Manager.getManagers().values()) {
            if (dm.getDataConfig().contains("players." + playerKey)) {
                Object value = dm.getDataConfig().get("players." + playerKey);

                if (value instanceof Integer) {
                    dm.setVariable(playerKey, (Integer) value);
                } else if (value instanceof String) {
                    dm.setVariable(playerKey, (String) value);
                }
            }
        }

        PlayerUtil.reset(player);
        if (!game.isStarted()) {
            player.teleport(Config.LOBBY_LOCATION);
        } else {
            game.getPlayers().get(player.getUniqueId()).setRole(GameRole.SEEKER);
            Common.sendMessage(player, "<aqua>あなたは途中参加したため、" + GameRole.SEEKER.getColoredName() + "として参加しました!");
            if (game.getCurrentTask() instanceof SeekerPhaseTask) {
                game.getMap().teleport(player);
                player.getInventory().addItem(new ItemBuilder(HideAndSeek.getInstance().getGame().getSettings().getSwordType().getItem()).name(HideAndSeek.getInstance().getGame().getSettings().getSwordType().getName()).unbreakable().enchantmentBoolean(Enchantment.FIRE_ASPECT, 1,game.getSettings().isSwordFire()).build(true));
            } else {
                player.teleport(Config.LOBBY_LOCATION);
            }
            Common.broadcastMessage("<yellow>" + player.getName() + "<aqua>は途中参加してきたので、" + GameRole.SEEKER.getColoredName() + "としてゲームに参加しました！");
        }
        event.joinMessage(Common.text("<gray>" + player.getName() +" <yellow>が参加しました (<aqua>" + Bukkit.getOnlinePlayers().size() + "<yellow>/<aqua>" + Bukkit.getMaxPlayers() + "<yellow>)!"));

        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
        Common.sendMessage(player,
                "<gray><strikethrough>                                                              <!strikethrough>",
                "",
                "<gold>              <bold> WELCOME BACK!",
                "",
                "<gray><strikethrough>                                                              <!strikethrough>"
        );
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Game game = HideAndSeek.Instance.getGame();

        game.getPlayers().remove(player.getUniqueId());
        if (HideAndSeek.getGamePlayerByPlayer(player) != null) {
            HideAndSeek.getGamePlayerByPlayer(player).setDisguises(null);
        }

        for (World w : Bukkit.getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (e.getMetadata(Game.DISGUISE_KEY).contains(new FixedMetadataValue(HideAndSeek.Instance, player.getUniqueId()))) {
                    e.remove();
                }
            }
        }

        event.quitMessage(Common.text("<gray>"+ player.getName() + " <yellow>が退出しました!"));

        if (game.isStarted() && game.canEnd()) {
            game.end();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {


        Game game = HideAndSeek.Instance.getGame();
        if (event instanceof EntityDamageByEntityEvent) {
            if (event.getEntity() instanceof Player entity && ((EntityDamageByEntityEvent) event).getDamager() instanceof Player damager) {
                GamePlayer gameEntity = game.getGamePlayer(entity);
                GamePlayer gameDamager = game.getGamePlayer(damager);

                if (gameEntity.getRole() == gameDamager.getRole()) {
                    event.setCancelled(true);
                    return;
                }
                if (gameEntity.getRole() == GameRole.HIDER && gameDamager.getRole() == GameRole.SEEKER) {
                    if (game.getSettings().isInstaKill()) {
                        event.setCancelled(true);
                        GamePlayerDeathEvent e = new GamePlayerDeathEvent(entity,damager);
                        e.callEvent();
                        if (e.isCancelled()) {
                            event.setDamage(0);
                        }
                    }
                }
            }
        }
        if (HideAndSeek.Instance.getGame().getState() != GameState.SEEKER_PHASE) {
            event.setCancelled(true);
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
            event.setCancelled(true);
            return;
        }

        Entity entity = event.getEntity();
        Player player;

        if (entity instanceof Player) {
            player = (Player) entity;
        } else if (entity.hasMetadata(Game.DISGUISE_KEY)) {
            player = Bukkit.getPlayer((UUID) entity.getMetadata(Game.DISGUISE_KEY).getFirst().value());
        } else {
            player = null;
        }

        if (player == null) {
            return;
        }

        double health = player.getHealth();
        if (health - event.getDamage() > 0) {
            return;
        }

        Entity attack = null;
        if (event instanceof EntityDamageByEntityEvent) {
            attack = ((EntityDamageByEntityEvent) event).getDamager();
        }
        if (attack != null) {
            GamePlayerDeathEvent e = new GamePlayerDeathEvent(player,(Player)attack);
            e.callEvent();
            if (e.isCancelled()) {
                event.setDamage(0);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Game game = HideAndSeek.Instance.getGame();
        if (event.getEntity() instanceof Player entity && event.getDamager() instanceof Player damager) {

            GamePlayer gameEntity = game.getGamePlayer(entity);
            GamePlayer gameDamager = game.getGamePlayer(damager);

            if (gameDamager.getRole() == GameRole.HIDER && gameEntity.getRole() == GameRole.SEEKER && event.getDamage() != 0.0) {
                gameDamager.setTrollPoint(gameDamager.getTrollPoint()+1);
                Common.sendMessage(gameDamager.getPlayer(), "<red>+1 トロールポイント! (シーカーを攻撃)");
            }

            if (gameEntity.getRole() == gameDamager.getRole()) {
                event.setCancelled(true);
                return;
            }

            if (gameEntity.getRole() == GameRole.SEEKER) {
                try {
                    ItemStack item = gameDamager.getPlayer().getInventory().getItemInMainHand();
                    if (item != null) {
                        if (item.equals(Items.STUN.getItem())) {
                            if (gameDamager.getStanCooldown() == 0 || gameDamager.getStanCooldown() < 0) {
                                entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 255, true, false));
                                entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 255, true, false));
                                entity.playSound(entity.getLocation(),Sound.BLOCK_ANVIL_FALL,1f,1f);
                                gameDamager.setStanCooldown(game.getSettings().getTimes().get("stun_cooldown"));
                            } else {
                                Common.sendMessage(gameDamager.getPlayer(), "<red><bold>COOLDOWN! <!bold>あなたのスタンはクールダウン中です。" + (gameDamager.getStanCooldown()) + "秒後に使用してください!");
                                event.setCancelled(true);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
                event.setDamage(0);
            }

            // TODO: 17/12/2022 Reveal block
        }
    }

    @EventHandler
    public void onDeath(GamePlayerDeathEvent event) {
        event.setCancelled(true);

        Game game = HideAndSeek.Instance.getGame();
        Player player = event.getPlayer();
        GamePlayer gamePlayer = game.getGamePlayer(player);

        Common.broadcastMessage(Messages.getDeathMessage(game.getGamePlayer(event.getAttacker()), gamePlayer.getPlayer().getName()));

        gamePlayer.getDisguises().getDisguise().stopDisguise();
        gamePlayer.setRole(GameRole.SEEKER);
        game.getMap().teleport(player);
        player.getInventory().clear();
        player.getInventory().addItem(new ItemBuilder(HideAndSeek.getInstance().getGame().getSettings().getSwordType().getItem()).name(HideAndSeek.getInstance().getGame().getSettings().getSwordType().getName()).unbreakable().enchantmentBoolean(Enchantment.FIRE_ASPECT, 1, game.getSettings().isSwordFire()).build(true));

        for (PotionEffect pe : player.getActivePotionEffects()) {
            if (pe.getType() == PotionEffectType.GLOWING) {
                player.removePotionEffect(PotionEffectType.GLOWING);
            }
        }


        game.getGamePlayer(event.getAttacker()).addXp(80);
        Common.sendMessage(gamePlayer.getPlayer(), "<gold>+80 経験値! (プレイヤーキル)");

        // Common.sendMessage(player, "", "<yellow>あなたは " + GameRole.SEEKER.getColoredName() + "<yellow> に見つかってしまったため、 " + gamePlayer.getRole().getColoredName() + "<yellow> になりました！");

        Common.sendMessage(player, "<yellow>勝利条件: <dark_aqua>" + gamePlayer.getRole().getGoal());
        player.setHealth(20.0D);
        if (game.canEnd()) {
            game.end();
        }
    }


    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if (HideAndSeek.getInstance().getGame().getGamePlayer(e.getPlayer()).isEnableBuild()) {
            if (LegacyComponentSerializer.legacySection().serialize(Objects.requireNonNull(e.line(0))).equalsIgnoreCase("[gamble]")) {
                e.line(0, Common.text("<gold><bold>[SLOT]"));
                e.line(3, Common.text("<green><b><u>CLICK TO PLAY<!u><!b>"));
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (event.getEntityType() == EntityType.ARROW) {
            projectile.remove();
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof Player) {
                continue;
            }
            entity.remove();
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        event.getWorld().setDifficulty(Difficulty.HARD);
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.DEFAULT) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack itemStack = event.getItem();
        Block block = event.getClickedBlock();
        Game game = HideAndSeek.Instance.getGame();
        GamePlayer gamePlayer = game.getGamePlayer(player);

        if (Items.COSMETIC.getItem().equals(itemStack)) {
            new CosmeticMenu().openMenu(player);
            event.setCancelled(true);
            return;
        }
        if (Items.PARKOUR_CHECKPOINT.getItem().equals(itemStack)) {
            if (gamePlayer.isParkour()) {
                player.teleport(gamePlayer.getCheckPoint());
                Common.sendMessage(player, "<green>最後のチェックポイントにテレポートしました!");
            }
            event.setCancelled(true);
            return;
        }
        if (Items.PARKOUR_CANCEL.getItem().equals(itemStack)) {
            if (gamePlayer.isParkour()) {
                gamePlayer.setParkour(false);
                Common.sendMessage(player, "<green>パルクールを終了しました!");
            }
            event.setCancelled(true);
            return;
        }
        if (Items.PARKOUR_SPAWN.getItem().equals(itemStack)) {
            if (gamePlayer.isParkour()) {
                player.teleport(gamePlayer.getParkourSpawn());
                Common.sendMessage(player, "<green>パルクールのスタートにテレポートしました!");
            }
            event.setCancelled(true);
            return;
        }

        if (!Items.TRANSFORM_TOOL.getItem().equals(itemStack) && block != null && block.getType().name().toUpperCase().contains("POT")) {
            event.setCancelled(true);
            return;
        }

        if (player.getGameMode() != GameMode.CREATIVE && action == Action.RIGHT_CLICK_BLOCK && block != null && block.getType() == Material.CHEST) {
            event.setCancelled(true);
            return;
        }

        if (game.isStarted()) {
            if (game.getGamePlayer(player).getRole() == GameRole.HIDER) {
                if (itemStack != null) {
                    if (itemStack.equals(Items.TRANSFORM_TOOL.getItem()) && block != null && block.getType() != Material.AIR && block.getType() != Material.BARRIER) {
                        BoundingBox box = block.getBoundingBox();

                        if ((!game.getSettings().isAllowUnbalancedBlocks() && box.getVolume() != 1)) {
                            Common.sendMessage(player, "<red>横の当たり判定が完全なブロックにのみ変形できます!");
                            return;
                        }

                        if (game.getSettings().isAntiCheatEnabled()) {
                            String[] disallowedBlocks = new String[]{"SIGN", "BUTTON", "DOOR", "LADDER", "HEAD", "BANNER", "SKULL", "DECORATED"};
                            for (String string : disallowedBlocks) {
                                if (block.getType().name().toUpperCase().contains(string)) {
                                    Common.sendMessage(player, "<red>あなたが変身しようとしたブロックは禁止されているブロックです！");
                                    return;
                                }
                            }
                        }

                        gamePlayer.getDisguises().getDisguise().stopDisguise();
                        gamePlayer.getDisguises().setType(DisguiseTypes.BLOCK);
                        gamePlayer.getDisguises().setData(block.getType().name());
                        gamePlayer.getDisguises().setDisguise(Util.disguise(player));
                        Common.sendMessage(player, "<yellow>あなたは <aqua>" + block.getType().name() + "<yellow> に変身しました！");
                    } else if (itemStack.equals(Items.TELEPORT_TOOL.getItem())) {
                        Location blockLoc = player.getLocation().getBlock().getLocation().clone();
                        Location blockLoc2 = player.getLocation().getBlock().getLocation().clone().add(0,-1,0);
                        Location blockLocSetBack = player.getLocation();

                        player.teleport(new Location(blockLoc.getWorld(), blockLoc.getX() + 0.5, blockLoc.getY(), blockLoc.getZ() + 0.5, player.getLocation().getYaw(), player.getLocation().getPitch()));

                        String[] disallowedBlocks = new String[]{"SIGN", "CARPET", "SLAB"};
                        String[] disallowedBlocks2 = new String[]{"GLASS_PANE", "FENCE"};

                        for (String string : disallowedBlocks) {
                            if (blockLoc2.getBlock().getType().name().toUpperCase().contains(string)) {
                                flag(gamePlayer, blockLocSetBack, game, player);
                                return;
                            }
                        }
                        for (String string : disallowedBlocks2) {
                            if (blockLoc.getBlock().getType().name().toUpperCase().contains(string)) {
                                flag(gamePlayer, blockLocSetBack, game, player);
                                return;
                            }
                        }
                        Common.sendMessage(player, "<green><bold>SUCCESSFULL! <!bold>正常に固定されました!");
                    } else if (itemStack.equals(Items.GLOWING_HINT.getItem())) {
                        if (gamePlayer.getGlowingHintCooldown() <= 0) {
                            if (gamePlayer.getRole() == GameRole.HIDER) {
                                gamePlayer.setTrollPoint(gamePlayer.getTrollPoint()+10);
                                Common.sendMessage(gamePlayer.getPlayer(), "<red>+10 トロールポイント! (発光ヒント)");
                                gamePlayer.setGlowingHintCooldown(20);
                                gamePlayer.addXp(50);
                                Common.sendMessage(gamePlayer.getPlayer(), "<gold>+50 経験値! (発光ヒント)");
                            }
                        } else {
                            Common.sendMessage(gamePlayer.getPlayer(),"<red>あなたの発光ヒントはクールダウン中です!\n" + gamePlayer.getGlowingHintCooldown() + " 秒後にお試しください!");
                        }
                    }
                }
            }
        }
    }

    public void flag(GamePlayer gamePlayer, Location blockLocSetBack, Game game, Player player) {
        if (!game.getSettings().isAntiCheatEnabled()) return;
        gamePlayer.getPlayer().teleport(blockLocSetBack);
        if (game.getSettings().getMaxFlag() >= 0) {
            return;
        }
        gamePlayer.setFlagged(gamePlayer.getFlagged()+1);
        if (gamePlayer.getFlagged() >= game.getSettings().getMaxFlag()) {
            gamePlayer.getDisguises().getDisguise().stopDisguise();
            gamePlayer.setRole(GameRole.SEEKER);
            game.getMap().teleport(player);
            player.getInventory().addItem(new ItemBuilder(HideAndSeek.getInstance().getGame().getSettings().getSwordType().getItem()).name(HideAndSeek.getInstance().getGame().getSettings().getSwordType().getName()).unbreakable().enchantmentBoolean(Enchantment.FIRE_ASPECT, 1,game.getSettings().isSwordFire()).build(true));
            Common.broadcastMessage("<dark_purple><bold>ANTI CHEAT! <!bold><aqua>" + gamePlayer.getPlayer().getName() + "<white> の違反回数が<aqua>" + gamePlayer.getFlagged() + "<white>回を超えたので、" + GameRole.SEEKER.getColoredName() + "<white> になりました!ww");
            if (game.canEnd()) {
                game.end();
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Block block = player.getLocation().getBlock();
        Block block2Above = player.getLocation().getBlock().getLocation().clone().add(0,-2,0).getBlock();
        GamePlayer gamePlayer = HideAndSeek.getGamePlayerByPlayer(player);


        if (!player.getWorld().equals(Bukkit.getWorld("world")) && HideAndSeek.Instance.getGame().isStarted()) {
            return;
        }

        if (gamePlayer != null && !HideAndSeek.getInstance().getGame().isStarted()) {

            if (player.getLocation().getY() <= -70) {
                if (gamePlayer.isParkour()) {
                    player.teleport(gamePlayer.getCheckPoint());
                } else {
                    player.teleport(Config.LOBBY_LOCATION);
                }
            }

            if (block.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
                if (block2Above.getType() == Material.WHITE_WOOL) {
                    if (gamePlayer.isParkour()) {
                        int lag = gamePlayer.getParkourTime();
                        gamePlayer.setParkourTime(0);
                        gamePlayer.setParkourTime2(0);
                        gamePlayer.setParkourStatus(CheckPointStatus.WHITE);
                        gamePlayer.setCheckPoint(player.getLocation());
                        gamePlayer.setParkourSpawn(player.getLocation());
                        if (lag > 10) {
                            Common.sendMessage(player, "<green>パルクール <gray>» <white>パルクールタイムがリセットされました!");
                        }
                    } else {
                        gamePlayer.setParkour(true);

                        player.getInventory().setItem(3,Items.PARKOUR_CHECKPOINT.getItem());
                        player.getInventory().setItem(4,Items.PARKOUR_SPAWN.getItem());
                        player.getInventory().setItem(5,Items.PARKOUR_CANCEL.getItem());

                        gamePlayer.setParkourTime(0);
                        gamePlayer.setParkourTime2(0);
                        gamePlayer.setParkourStatus(CheckPointStatus.WHITE);
                        gamePlayer.setCheckPoint(player.getLocation());
                        gamePlayer.setParkourSpawn(player.getLocation());
                        Common.sendMessage(player, "<green>パルクール <gray>» <white>パルクールチャレンジが始まりました!");
                    }
                } else if (block2Above.getType() == Material.BLUE_WOOL) {
                    if (gamePlayer.getParkourStatus() != CheckPointStatus.LIGHT_BLUE) {
                        return;
                    }
                    Common.sendMessage(player, "<gold>パルクール <gray>» <white>パルクールを終了しました!\n<white>あなたのタイム:<blue> "  + Util.getSecFromTick(gamePlayer.getParkourTime()));

                    Manager.getDataManager(ParkourDataManager.class).addPlayerInfoInteger(player.getUniqueId().toString(), gamePlayer.getParkourTime(), DataEnum.DataManagerType.BETTER);

                    gamePlayer.setParkour(false);
                    gamePlayer.setParkourStatus(CheckPointStatus.NOTPLAYING);

                    gamePlayer.setParkourTime(0);
                    gamePlayer.setParkourTime2(0);
                }
            } else if (block.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                if (block2Above.getType().name().equalsIgnoreCase(gamePlayer.getParkourStatus().next() + "_WOOL")) {
                    if (gamePlayer.isParkour()) {
                        gamePlayer.setParkourStatus(gamePlayer.getParkourStatus().next());
                        gamePlayer.setCheckPoint(player.getLocation());
                        Common.sendMessage(player, "", "<green><bold>PARKOUR! <!bold>チェックポイントに到達しました!\n<gray>| <white>現在のタイム:<blue> "  + Util.getSecFromTick(gamePlayer.getParkourTime()) + "\n<gray>| <white>前のチェックポイントからのタイム:<blue> "+Util.getSecFromTick(gamePlayer.getParkourTime2()), "");

                        gamePlayer.parkourLap();
                    } else {
                        Common.sendMessage(player, "<green><bold>PARKOUR! <!bold>あなたはパルクールをしていません!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        Player p = (Player) e.getWhoClicked();
        if (item == null) return;

        if (p.getGameMode() != GameMode.CREATIVE) e.setCancelled(true);

        GamePlayer gp = HideAndSeek.getGamePlayerByPlayer(p);
        ClickType click = e.getClick();

        for (Menu menu : Registers.menus) {
            if (!menu.getClass().equals(gp.getMenu().getClass())) continue;

            for (Button btn : menu.getButtons(p).values()) {
                if (btn.getButtonItem(p).isSimilar(item) && !HideAndSeek.Instance.getGame().isStarted()) {
                    clicked(btn, p, click);
                    return;
                }
            }
        }
    }

    public void clicked(Button b, Player player, ClickType clickType) {
        if (b instanceof IntegerButton iButton) {
            switch (clickType) {
                case LEFT:
                    iButton.plus1(player);
                    break;
                case SHIFT_LEFT:
                    iButton.plus10(player);
                    break;
                case RIGHT:
                    iButton.minus1(player);
                    break;
                case SHIFT_RIGHT:
                    iButton.minus10(player);
                    break;
                default:
                    Common.sendMessage(player,"<red>このボタンを作動させるには、クリックが必要です。");
            }
            iButton.clicked(player, clickType);
        } else if (b instanceof ToggleButton tButton) {
            if (Objects.requireNonNull(clickType) == ClickType.LEFT) {
                tButton.clicked(player, clickType);
            } else {
                Common.sendMessage(player, "<red>このボタンを作動させるには、クリックが必要です。");
            }
        } else {
            b.clicked(player, clickType);
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String message = LegacyComponentSerializer.legacySection().serialize(event.message());
        event.setCancelled(true);

        GamePlayer gamePlayer = HideAndSeek.getInstance().getGame().getGamePlayer(player);

        if (gamePlayer.previousChat != null && (message.startsWith(gamePlayer.previousChat) || gamePlayer.previousChat.equalsIgnoreCase(message))) {
            Common.sendMessage(player,Common.text("<red>チャットをスパムしないでください！"));
            return;
        }
        gamePlayer.previousChat = message;

        if (HideAndSeek.getInstance().getGame().getGamePlayer(player).isMuted()) {
            Common.sendMessage(player,Common.text("<gold><strikethrough>                                     <bold>"),Common.text("<red>          <bold>MUTE"),Common.text(""),Common.text("<gold>あなたは現在muteされています!"),Common.text("<gray>matanku network"),Common.text("<gold><strikethrough>                                     <bold>"));
            return;
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            Common.sendMessage(p,Common.text(gamePlayer.getPrestigeFormat() + " <gray>" + player.getName() + "<white>: " + message));
        }
    }

}
