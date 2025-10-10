package matanku.kakurembo.game.disguise;

import lombok.Getter;
import lombok.Setter;
import matanku.kakurembo.disguise.Disguise;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.game.Game;
import matanku.kakurembo.util.Util;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DisguiseData {

    private String data;
    private Disguise disguise;
    public DisguiseData( String data) {
        this.data = data;
    }
}
