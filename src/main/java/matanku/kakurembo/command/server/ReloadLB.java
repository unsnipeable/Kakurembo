package matanku.kakurembo.command.server;

import matanku.kakurembo.command.ServerCommand;
import matanku.kakurembo.config.datamanager.Manager;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.util.enums.RankEnum;
import org.bukkit.entity.Player;

public class ReloadLB extends ServerCommand {
    public ReloadLB() {
        super("reloadlb");
        this.setPerm(RankEnum.ADMIN);
        this.setAliases(new String[]{"rlb", "reloadleaderboard"});
    }

    @Override
    public void run(Player player, String[] args, String command) {
        Manager.register(true);
        Common.sendMessage(player, "<gray>Successfully Reloaded Leaderboards");
    }
}
