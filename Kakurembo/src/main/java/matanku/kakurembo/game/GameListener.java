package matanku.kakurembo.game;

import matanku.kakurembo.Config;
import matanku.kakurembo.Items;
import matanku.kakurembo.enums.DisguiseTypes;
import matanku.kakurembo.enums.GameRole;
import matanku.kakurembo.enums.GameState;
import matanku.kakurembo.player.GamePlayer;
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
            game.getMap().teleport(player);
            player.getInventory().addItem(new ItemStack(Material.NETHERITE_SWORD));
            Common.broadcastMessage("<yellow>" + player.getName() + "<aqua>は途中参加してきたので、" + GameRole.SEEKER.getColoredName() + "としてゲームに参加しました！");
        }
        event.joinMessage();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Game game = HideAndSeek.INSTANCE.getGame();

        game.getPlayers().remove(player.getUniqueId());

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
                                    Common.sendMessage(player, "<red>あなたが固定しようとしたブロックは禁止されているブロックです！");
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
            Common.broadcastMessage("<dark_purple><bold>ANTI CHEAT! <!bold><aqua>" + gamePlayer.getPlayer().getName() + "<white> の違反回数が<aqua>" + "<white>回を超えたので、" + GameRole.SEEKER.getColoredName() + "<white> になりました!ww");
            if (game.canEnd()) {
                game.end();
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

        event.setCancelled(true);
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
