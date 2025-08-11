package matanku.kakurembo.util;

import matanku.kakurembo.game.amongUs.GameAmongUsTask;
import matanku.kakurembo.game.amongUs.tasks.CafeteriaOchibaTask;

import java.util.ArrayList;

public class AmongUsUtil {
    public static GameAmongUsTask[] allTasks() {
        return new GameAmongUsTask[]{
                new CafeteriaOchibaTask()
        };
    }


    public static GameAmongUsTask[] generateTasks(int tasks) {
        ArrayList<GameAmongUsTask> task = new ArrayList<>();
        for (int i = 0; i < tasks; i++) {
            task.add(Util.random(allTasks()));
        }
        return task.toArray(new GameAmongUsTask[tasks]);
    }
}
