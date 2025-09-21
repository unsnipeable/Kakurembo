package matanku.kakurembo.command;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.api.util.Checker;
import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.api.util.serialization.LocationSerialization;
import matanku.kakurembo.config.Config;
import matanku.kakurembo.config.datamanager.Manager;
import matanku.kakurembo.game.task.impl.SeekerPhaseTask;
import matanku.kakurembo.menu.GameSettingsMenu;
import matanku.kakurembo.menu.RoleSelectAdminMenu;
import matanku.kakurembo.player.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class HideAndSeekCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Common.sendMessage(commandSender, Arrays.toString(strings));
        switch (strings.length) {
            case 1: {
                switch (strings[0].toLowerCase()) {
                    case "restart": {
                        if (!commandSender.hasPermission("op")) {
                            Common.sendMessage(commandSender, "<red>あなたはこのコマンドを使用する権限がありません!");
                        } else {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.kick(Common.text("<white>参加いただきありがとうございます! \n今から<light_purple><bold>再起動<!bold><white>を行います。\nしばらくしてから再参加(リログ)してください。\n\n<gray>2025 Matanku, Hide And Seek Network"));
                            }
                            Bukkit.shutdown();
                        }
                        break;
                    }
                    case "reload": {
                        if (!commandSender.hasPermission("op")) {
                            Common.sendMessage(commandSender, "<red>あなたはこのコマンドを使用する権限がありません!");
                        } else {
                            Manager.register();
                            Common.sendMessage(commandSender, "<gray>Successfully Reloaded Leaderboards");
                        }
                        break;
                    }
                    case "admin", "gameadmin": {
                        showHelp(commandSender);
                         break;
                    }
                }
                break;
            }
            case 2: {
                switch (strings[0].toLowerCase()) {
                    case "admin": {
                        if (!commandSender.hasPermission("op")) {
                            Common.sendMessage(commandSender, "<red>あなたはこのコマンドを使用する権限がありません!");
                            return true;
                        }
                        switch (strings[1].toLowerCase()) {
                            case "setlobby": {

                                Location location = ((Player)commandSender).getLocation();

                                Config.LOBBY_LOCATION = location;
                                HideAndSeek.INSTANCE.getConfigFile().getConfiguration().set("lobby-location", LocationSerialization.serializeLocation(location));
                                HideAndSeek.INSTANCE.getConfigFile().save();

                                Common.sendMessage(((Player)commandSender), "<green>ロビーを設定しました!");
                                break;
                            }
                            case "whitelist": {

                                Config.WHITELIST = !Config.WHITELIST;
                                HideAndSeek.INSTANCE.getConfigFile().getConfiguration().set("whitelist", Config.WHITELIST);
                                HideAndSeek.INSTANCE.getConfigFile().save();

                                Common.sendMessage(((Player)commandSender), "<green>ホワイトリストを" + Config.WHITELIST + "に設定しました。");
                                break;
                            }
                        }
                        showHelp(commandSender);
                        break;
                    }
                    case "gameadmin": {
                        if (!commandSender.hasPermission("gameadmin")) {
                            Common.sendMessage(commandSender, "<red>あなたはこのコマンドを使用する権限がありません!");
                            return true;
                        }
                        switch (strings[1].toLowerCase()) {
                            case "settings": {
                                new GameSettingsMenu().openMenu((Player)commandSender);
                                break;
                            }
                            case "roleselect": {
                                new RoleSelectAdminMenu().openMenu((Player)commandSender);
                                break;
                            }
                            case "end":{
                                try {
                                    HideAndSeek.getINSTANCE().getGame().end();
                                } catch (AssertionError error) {
                                    Common.sendMessage((Player)commandSender, Common.text("<red>" + error.getMessage()));
                                }
                                break;
                            }
                        }
                        showHelp(commandSender);
                        break;
                    }
                }
                break;
            }
            case 3: {
                switch (strings[0].toLowerCase()) {
                    case "admin": {
                        if (!commandSender.hasPermission("admin")) {
                            Common.sendMessage(commandSender, "<red>あなたはこのコマンドを使用する権限がありません!");
                            return true;
                        }
                        switch (strings[1].toLowerCase()) {
                            case "builder": {
                                GamePlayer gp = null;
                                for (GamePlayer ga : HideAndSeek.INSTANCE.getGame().getPlayers().values()) {
                                    if (ga.getPlayer().getName().contains(strings[2])) {
                                        gp = ga;
                                    }
                                }
                                if (gp == null) return false;
                                gp.setEnableBuild(!gp.isEnableBuild());
                                if (gp.isEnableBuild()) {
                                    gp.getPlayer().setGameMode(GameMode.CREATIVE);
                                    Common.sendMessage(gp.getPlayer(), "<green>あなたにビルド権限が渡りました!");
                                } else {
                                    gp.getPlayer().setGameMode(GameMode.SURVIVAL);
                                    Common.sendMessage(gp.getPlayer(), "<red>あなたからビルド権限が剥奪されました!");
                                }
                                break;
                            }
                            case "setcoin": {
                                GamePlayer gp = null;
                                for (GamePlayer ga : HideAndSeek.INSTANCE.getGame().getPlayers().values()) {
                                    if (ga.getPlayer().getName().equalsIgnoreCase(strings[2])) {
                                        gp = ga;
                                    }
                                }
                                if (gp != null) {
                                    if (Checker.isInteger(strings[2])) {
                                        gp.setCoin(Integer.parseInt(strings[2]));
                                    }
                                }
                                break;
                            }
                            case "mute": {
                                GamePlayer gp = null;
                                for (GamePlayer ga : HideAndSeek.INSTANCE.getGame().getPlayers().values()) {
                                    if (ga.getPlayer().getName().contains(strings[2])) {
                                        gp = ga;
                                    }
                                }
                                if (gp == null) return false;
                                gp.setMuted(!gp.isMuted());
                                Common.sendMessage(commandSender, "<red>" + gp.getPlayer().getName() + "を" + (gp.isMuted()?"":"アン") + "ミュートしました。");
                                break;
                            }
                        }
                        showHelp(commandSender);
                        break;
                    }
                    case "gameadmin": {
                        if (!commandSender.hasPermission("gameadmin")) {
                            Common.sendMessage(commandSender, "<red>あなたはこのコマンドを使用する権限がありません!");
                            return true;
                        }
                        switch (strings[1].toLowerCase()) {
                            case "tracker": {

                                if (!HideAndSeek.INSTANCE.getGame().isStarted()) {
                                    Common.sendMessage(commandSender, "<red>ゲームが開始されていません。");
                                    return false;
                                }

                                if (Checker.isBoolean(strings[2])) {
                                    SeekerPhaseTask.setTracker(Boolean.parseBoolean(strings[2]));

                                    Common.sendMessage(commandSender, "<green>成功");
                                    return true;
                                }
                                break;
                            }
                            case "countdown": {
                                if (HideAndSeek.INSTANCE.getGame().getSettings().getMap() == null) {
                                    Common.sendMessage(commandSender, "<red>カウントダウンを開始しようとしたときにエラーが発生しました: マップがまだ選択されていません! '/settings'コマンドを使用してマップを選択してください！");
                                    return false;
                                }

                                if (Checker.isInteger(strings[2])) {
                                    HideAndSeek.INSTANCE.getGame().startCountdown(Integer.parseInt(strings[2]));

                                    Common.sendMessage(commandSender, "<green>カウントダウンが始まります！");
                                    return true;
                                }

                                Common.sendMessage(commandSender, "<red>使い方: /countdown <int>");

                                break;
                            }
                        }
                        showHelp(commandSender);
                        break;
                    }
                }
                break;
            }
        }
        return false;
    }

    public void showHelp(CommandSender cs) {
        showHelp((Player)cs);
    }
    public void showHelp(Player p) {
        Common.sendMessage(p, "<gold>[HideAndSeek] <white>Commands: "
                ," <gold>/has restart<gray>: Close the server"
                ," <gold>/has reload<gray>: Reload the leaderboards"
                ," <gold>/has admin<gray>: Admin Commands"
                ," <gold>/has gameadmin<gray>: Admin only Game Commands"
        );
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.hasPermission("admin") || commandSender.hasPermission("gameadmin")) {

            switch (strings.length) {
                case 1: {
                    // /hideandseek <WHAT?>
                    return Arrays.asList("restart", "reload", "admin", "gameadmin");
                }
                case 2: {
                    // /hideandseek <WHAT?>
                    switch (strings[0].toLowerCase()) {
                        // /hideandseek admin/game <WHAT?>
                        case "admin":
                            return Arrays.asList("builder", "setcoin", "setlobby", "mute", "whitelist");
                        case "gameadmin":
                            return Arrays.asList("settings", "tracker", "countdown", "roleselect", "end");
                    }
                }
                case 3: {
                    // /hideandseek <WHAT?>
                    switch (strings[0].toLowerCase()) {
                        // /hideandseek admin/game <WHAT?>
                        case "admin": {
                            switch (strings[1].toLowerCase()) {
                                // /hideandseek admin/game builder/setcoin <WHAT?>
                                case "builder", "setcoin", "mute":
                                    return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
                            }
                        }
                        case "gameadmin": {
                            // /hideandseek admin/game tracker <true/false>
                            switch (strings[1].toLowerCase()) {
                                case "tracker":
                                    return Arrays.asList("true", "false");
                            }
                        }
                    }
                }
            }
        }
        return List.of();
    }
}
