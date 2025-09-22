package matanku.kakurembo.game.disguise;

import lombok.Getter;
import lombok.Setter;
import matanku.kakurembo.disguise.Disguise;
import matanku.kakurembo.enums.DisguiseTypes;
import matanku.kakurembo.util.Util;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.game.Game;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DisguiseData {

    private DisguiseTypes type;
    private String data;
    private Disguise disguise;

    public DisguiseData() {
        Game game = HideAndSeek.Instance.getGame();

        List<DisguiseTypes> types = new ArrayList<>();
        if (!HideAndSeek.Instance.getMapFile().getStringList("maps." + game.getSettings().getMap() + ".disguises.blocks").isEmpty()) {
            types.add(DisguiseTypes.BLOCK);
        }
        if (!HideAndSeek.Instance.getMapFile().getStringList("maps." + game.getSettings().getMap() + ".disguises.mobs").isEmpty()) {
            types.add(DisguiseTypes.MOB);
        }

        this.type = Util.random(types);
        if (this.type == DisguiseTypes.BLOCK) {
            this.data = Util.random(HideAndSeek.Instance.getMapFile().getStringList("maps." + game.getSettings().getMap() + ".disguises.blocks"));
        } else if (this.type == DisguiseTypes.MOB) {
            this.data = Util.random(HideAndSeek.Instance.getMapFile().getStringList("maps." + game.getSettings().getMap() + ".disguises.mobs"));
        } else {
            throw new NullPointerException("Cannot find a suitable DisguiseTypes");
        }
    }

    public DisguiseData(DisguiseTypes type, String data) {
        this.type = type;
        this.data = data;
    }

}
