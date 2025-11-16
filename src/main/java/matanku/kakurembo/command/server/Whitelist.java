package matanku.kakurembo.command.server;

import matanku.kakurembo.command.ServerCommand;
import matanku.kakurembo.config.Config;
import matanku.kakurembo.util.Common;
import matanku.kakurembo.util.enums.RankEnum;
import org.bukkit.entity.Player;

public class Whitelist extends ServerCommand {
    public Whitelist() {
        super("whitelistenabled");
        this.setPerm(RankEnum.ADMIN);
        this.setAliases(new String[]{"wle"});
    }

    @Override
    public void run(Player player, String[] args, String command) {
        Config.WHITELIST = !Config.WHITELIST;
        Common.sendMessage(player, "<green>ホワイトリストを" + String.valueOf(Config.WHITELIST).toUpperCase() + "に設定しました。");
    }
}
