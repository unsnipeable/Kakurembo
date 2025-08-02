package matanku.kakurembo.game;

import matanku.kakurembo.Config;
import matanku.kakurembo.Items;
import matanku.kakurembo.enums.CheckPointStatus;
import matanku.kakurembo.enums.DisguiseTypes;
import matanku.kakurembo.enums.GameRole;
import matanku.kakurembo.enums.GameState;
import matanku.kakurembo.game.task.impl.HiderPhaseTask;
import matanku.kakurembo.game.task.impl.SeekerPhaseTask;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.util.ParkourUtil;
import matanku.kakurembo.util.PlayerUtil;
import matanku.kakurembo.util.Util;
import matanku.kakurembo.api.menu.Button;
import matanku.kakurembo.api.menu.Menu;
import matanku.kakurembo.api.menu.Registers;
import matanku.kakurembo.api.menu.button.IntegerButton;
import matanku.kakurembo.api.menu.button.ToggleButton;
import matanku.kakurembo.api.menu.pagination.PaginatedMenu;
import matanku.kakurembo.api.util.Common;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.event.GamePlayerDeathEvent;

import java.util.Objects;
import java.util.UUID;

public class GameListener implements Listener {

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        Game game = HideAndSeek.INSTANCE.getGame();

        if (game.isStarted()) {
            if (!game.getSettings().isAllowJoinAsSeekerAfterStarted()) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Common.text("<red>管理者が途中参加設定をオフにしているため、ゲームの途中にログインできません。"));
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Game game = HideAndSeek.INSTANCE.getGame();

        game.getPlayers().putIfAbsent(player.getUniqueId(), new GamePlayer(player.getUniqueId()));

        PlayerUtil.reset(player);
        if (!game.isStarted()) {
            player.teleport(Config.LOBBY_LOCATION);
            event.joinMessage(Common.text("<gray>[<green>+<gray>] <yellow>" + player.getName()));
        } else {
            game.getPlayers().get(player.getUniqueId()).setRole(GameRole.SEEKER);
            Common.sendMessage(player, "<aqua>あなたは途中参加したため、" + GameRole.SEEKER.getColoredName() + "として参加しました!");
            if (game.getCurrentTask() instanceof SeekerPhaseTask) {
                game.getMap().teleport(player);
                player.getInventory().addItem(new ItemStack(Material.NETHERITE_SWORD));
            } else {
                player.teleport(Config.LOBBY_LOCATION);
            }
            Common.broadcastMessage("<yellow>" + player.getName() + "<aqua>は途中参加してきたので、" + GameRole.SEEKER.getColoredName() + "としてゲームに参加しました！");
        }
        event.joinMessage();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Game game = HideAndSeek.INSTANCE.getGame();

        game.getPlayers().remove(player.getUniqueId());
        if (HideAndSeek.getGamePlayerByPlayer(player) != null) {
            HideAndSeek.getGamePlayerByPlayer(player).setDisguises(null);
        }

        event.quitMessage(Common.text("<gray>[<red>-<gray>] <yellow>" + player.getName()));

        if (game.isStarted() && game.canEnd()) {
            game.end();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {


        Game game = HideAndSeek.INSTANCE.getGame();
        if (event instanceof EntityDamageByEntityEvent) {
            if (event.getEntity() instanceof Player entity && ((EntityDamageByEntityEvent) event).getDamager() instanceof Player damager) {
                GamePlayer gameEntity = game.getGamePlayer(entity);
                GamePlayer gameDamager = game.getGamePlayer(damager);

                if (gameEntity.getRole() == gameDamager.getRole()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if (HideAndSeek.INSTANCE.getGame().getState() != GameState.SEEKER_PHASE) {
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
        Game game = HideAndSeek.INSTANCE.getGame();
        if (event.getEntity() instanceof Player entity && event.getDamager() instanceof Player damager) {
            GamePlayer gameEntity = game.getGamePlayer(entity);
            GamePlayer gameDamager = game.getGamePlayer(damager);

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
                                gameDamager.setStanCooldown(60);
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

        Game game = HideAndSeek.INSTANCE.getGame();
        Player player = event.getPlayer();
        GamePlayer gamePlayer = game.getGamePlayer(player);

        Common.broadcastMessage("<red><bold>ELIMINATE! <!bold>" +gamePlayer.getRole().getColor() + player.getName() + "<red>は" + event.getAttacker().getName() +  "に倒され，" + GameRole.SEEKER.getColoredName() + " <red>になりました!");

        gamePlayer.getDisguises().getDisguise().stopDisguise();
        gamePlayer.setRole(GameRole.SEEKER);
        game.getMap().teleport(player);
        player.getInventory().setContents(GameRole.SEEKER.getTools());

        for (PotionEffect pe : player.getActivePotionEffects()) {
            if (pe.getType() == PotionEffectType.GLOWING) {
                player.removePotionEffect(PotionEffectType.GLOWING);
            }
        }
        Common.sendMessage(player, "","<yellow>あなたは " + GameRole.SEEKER.getColoredName() + "<yellow> に見つかってしまったため、 " + gamePlayer.getRole().getColoredName() + "<yellow> になりました！");

        Common.sendMessage(player, "<yellow>勝利条件: <dark_aqua>" + gamePlayer.getRole().getGoal());
        player.setHealth(20.0D);
        if (game.canEnd()) {
            game.end();
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
        Game game = HideAndSeek.INSTANCE.getGame();
        GamePlayer gamePlayer = game.getGamePlayer(player);

        if (Items.PARKOUR_CHECKPOINT.getItem().equals(itemStack)) {
            if (gamePlayer.isParkour()) {
                player.teleport(gamePlayer.getCheckPoint());
                Common.sendMessage(player, "最後のチェックポイントにテレポートしました!");
            }
            event.setCancelled(true);
            return;
        }
        if (Items.PARKOUR_CANCEL.getItem().equals(itemStack)) {
            if (gamePlayer.isParkour()) {
                gamePlayer.setParkour(false);
                Common.sendMessage(player, "パルクールを終了しました!");
            }
            event.setCancelled(true);
            return;
        }
        if (Items.PARKOUR_SPAWN.getItem().equals(itemStack)) {
            if (gamePlayer.isParkour()) {
                player.teleport(gamePlayer.getParkourSpawn());
                Common.sendMessage(player, "パルクールのスタートにテレポートしました!");
            }
            event.setCancelled(true);
            return;
        }

        if (!Items.TRANSFORM_TOOL.getItem().equals(itemStack) && block != null && block.getType().name().toUpperCase().contains("POTTED")) {
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
                            String[] disallowedBlocks = new String[]{"SIGN", "BUTTON", "DOOR", "LADDER", "HEAD", "BANNER"};
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

                        String[] disallowedBlocks = new String[]{"SIGN", "CARPET"};
                        String[] disallowedBlocks2 = new String[]{"STAINED_GLASS_PANE"};

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
                    }
                }
            }
        }
    }

    public void flag(GamePlayer gamePlayer, Location blockLocSetBack, Game game, Player player) {
        if (!game.getSettings().isAntiCheatEnabled()) return;
        gamePlayer.getPlayer().teleport(blockLocSetBack);
        gamePlayer.setFlagged(gamePlayer.getFlagged()+1);
        if (gamePlayer.getFlagged() >= 20) {
            gamePlayer.getDisguises().getDisguise().stopDisguise();
            gamePlayer.setRole(GameRole.SEEKER);
            game.getMap().teleport(player);
            player.getInventory().setContents(GameRole.SEEKER.getTools());
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


        if (!(player.getWorld().getBlockAt(new Location(player.getWorld(),8,-60,5)).getType() == Material.SEA_LANTERN)) {
            return;
        }
        if (gamePlayer != null) {

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
                        gamePlayer.setCheckPoint(player.getLocation().getBlock().getLocation());
                        if (lag > 10) {
                            Common.sendMessage(player, "<green><bold>PARKOUR! <!bold>パルクールタイムがリセットされました!");
                        }
                    } else {
                        gamePlayer.setParkour(true);
                        gamePlayer.setParkourTime(0);
                        gamePlayer.setParkourTime2(0);
                        gamePlayer.setParkourStatus(CheckPointStatus.WHITE);
                        gamePlayer.setCheckPoint(player.getLocation().getBlock().getLocation());
                        Common.sendMessage(player, "<green><bold>PARKOUR! <!bold>パルクールチャレンジが始まりました!");
                    }
                } else if (block2Above.getType() == Material.BLUE_WOOL) {
                    if (gamePlayer.getParkourStatus() != CheckPointStatus.LIGHT_BLUE) {
                        return;
                    }
                    Common.sendMessage(player, "<green><bold>PARKOUR! <!bold>パルクールを終了しました!\n<white>あなたのタイム:<blue> "  + Util.getSecFromTick(gamePlayer.getParkourTime()));
                    ParkourUtil.addPlayerInfo(player.getName(), gamePlayer.getParkourTime());
                    gamePlayer.setParkour(false);
                    gamePlayer.setParkourStatus(CheckPointStatus.NOTPLAYING);

                    gamePlayer.setParkourTime(0);
                    gamePlayer.setParkourTime2(0);
                }
            } else if (block.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                if (block2Above.getType().name().equalsIgnoreCase(gamePlayer.getParkourStatus().next() + "_WOOL")) {
                    if (gamePlayer.isParkour()) {
                        gamePlayer.setParkourStatus(gamePlayer.getParkourStatus().next());
                        gamePlayer.setCheckPoint(player.getLocation().getBlock().getLocation());
                        Common.sendMessage(player, "<green><bold>PARKOUR! <!bold>チェックポイントに到達しました!\n<white>現在のタイム:<blue> "  + Util.getSecFromTick(gamePlayer.getParkourTime()) + "\n<white>前のチェックポイントからのタイム:<blue> "+Util.getSecFromTick(gamePlayer.getParkourTime2()));

                        gamePlayer.parkourLap();
                    } else {
                        Common.sendMessage(player, "<green><bold>PARKOUR! <!bold>あなたはパルクールをしていません!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        ItemStack is = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        ClickType clickType = event.getClick();
        if (is == null) return;

        for (Menu menu : Registers.menus) {
            menu.getButtons(player).forEach((k, v) -> {
                if (v.getButtonItem(player).isSimilar(is)) {
                    clicked(v,player, clickType);
                }
            });
        }
        for (PaginatedMenu menu : Registers.paginatedMenus) {
            menu.getAllPagesButtons(player).forEach((k, v) -> {
                if (v.getButtonItem(player).isSimilar(is)) {
                    clicked(v,player, clickType);
                }
            });
        }

        for (GamePlayer ga : HideAndSeek.INSTANCE.getGame().getPlayers().values()) {
            if (ga.getPlayer().getName().contains(player.getName())) {
                if (!ga.isEnableBuild())
                    event.setCancelled(true);
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


}
