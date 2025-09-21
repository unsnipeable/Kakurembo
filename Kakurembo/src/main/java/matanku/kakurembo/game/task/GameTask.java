package matanku.kakurembo.game.task;

import matanku.kakurembo.api.util.TaskTicker;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.game.Game;

public abstract class GameTask extends TaskTicker {

    public final int seconds;

    public GameTask(int seconds) {
        super(0, 20, false);
        this.seconds = seconds;
    }

    public final Game game = HideAndSeek.INSTANCE.getGame();


}
