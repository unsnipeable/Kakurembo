package matanku.kakurembo.game.task;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.game.Game;
import matanku.kakurembo.util.TaskTicker;

public abstract class GameTask extends TaskTicker {

    public final int seconds;

    public GameTask(int seconds) {
        super(0, 20, false);
        this.seconds = seconds;
    }

    public final Game game = HideAndSeek.Instance.getGame();
}
