package matanku.kakurembo.game.amongUs;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.api.menu.Menu;
import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.game.Game;
import matanku.kakurembo.player.GamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class GameAmongUsTask extends Menu {
    public void clear(Player player) {
        GamePlayer gamePlayer = HideAndSeek.getGamePlayerByPlayer(player);
        Game game = HideAndSeek.getINSTANCE().getGame();
        player.closeInventory();
        if (gamePlayer != null) {
            gamePlayer.setDoneTasks(gamePlayer.getDoneTasks()+1);
            game.setCompTask(game.getCompTask()+1);
        }
    }
    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public Component getTitle(Player player) {
        return Common.text("タスク");
    }
}
