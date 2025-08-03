package matanku.kakurembo.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@RequiredArgsConstructor
public class Party {
    public GamePlayer leader;
    public ArrayList<GamePlayer> member = new ArrayList<>();
    public ArrayList<GamePlayer> invites = new ArrayList<>();
}
