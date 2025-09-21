package matanku.kakurembo.disguise.impl;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.disguise.Disguise;
import matanku.kakurembo.game.Game;

@Getter
public class MiscDisguise extends Disguise {

    private final Material material;

    private BlockDisplay blockDisplay;
    private Interaction interaction;

    public MiscDisguise(Player player, Material material) {
        super(player);
        this.material = material;
    }

    @Override
    public void startDisguise() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, false));
        player.setCollidable(false);

        if (!material.isBlock()) {
            throw new UnsupportedOperationException("The selected material " + material.name() + " is not a block");
        }

        blockDisplay = player.getLocation().getWorld().spawn(player.getLocation(), BlockDisplay.class, display -> {
            display.setBlock(Bukkit.createBlockData(material));
            display.setMetadata(Game.DISGUISE_KEY, new FixedMetadataValue(HideAndSeek.INSTANCE, player.getUniqueId()));
            display.setTeleportDuration(1);
        });


        //todo: add an interaction entity which size matches the block display so seeker can easily hit the hider


        task = new BukkitRunnable() {
            @Override
            public void run() {
                Location location = player.getLocation().clone().add(-0.5, 0, -0.5);
                location.setYaw(0);
                location.setPitch(0);

                blockDisplay.teleport(location);

            }
        }.runTaskTimer(HideAndSeek.INSTANCE, 0, 1L);
    }

    @Override
    public void stopDisguise() {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.setCollidable(true);

        task.cancel();
        blockDisplay.remove();
    }
}
