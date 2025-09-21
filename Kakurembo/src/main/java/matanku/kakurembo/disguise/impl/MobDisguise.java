package matanku.kakurembo.disguise.impl;

import lombok.Getter;
import matanku.kakurembo.disguise.Disguise;
import org.bukkit.entity.Player;

@Getter
public class MobDisguise extends Disguise {

    private final String mob;

    public MobDisguise(Player player, String mob) {
        super(player);
        this.mob = mob;
    }

    @Override
    public void startDisguise() {

    }

    @Override
    public void stopDisguise() {

    }
}
