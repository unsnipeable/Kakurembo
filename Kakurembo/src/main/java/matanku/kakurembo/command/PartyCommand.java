package matanku.kakurembo.command;

import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.api.util.Common;
import matanku.kakurembo.menu.GameSettingsMenu;
import matanku.kakurembo.menu.RoleSelectAdminMenu;
import matanku.kakurembo.player.GamePlayer;
import matanku.kakurembo.player.Party;
import matanku.kakurembo.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class PartyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender instanceof ConsoleCommandSender) return false;

        Player player = (Player)commandSender;
        GamePlayer gamePlayer = HideAndSeek.getGamePlayerByPlayer(player);

        if (gamePlayer == null || args == null || args[0].equalsIgnoreCase("help")) {
            Common.sendMessage(player, helpMessage);
            return false;
        }

        switch (args.length) {
            case 1:
                switch (args[0].toLowerCase()) {
                    case "invite":
                    case "accept":
                    case "kick":
                        log(player, helpMessage);
                        break;
                    case "list":
                        ArrayList<String> list = new ArrayList<>();
                        list.add("現在のParty:");
                        list.add("リーダー: <gray>" + gamePlayer.getParty().getLeader());
                        if (gamePlayer.getParty().getMember() == null) {
                            list.add("メンバー: <gray>なし");
                        } else {
                            list.add("メンバー: <gray>" + gamePlayer.getParty().getMember());
                        }
                        log(gamePlayer, list.toArray(new String[0]));
                        break;
                    case "disband":
                        if (gamePlayer.getParty() == null) {
                            log(player, "あなたはPartyに入っていません!");
                        } else {
                            if (gamePlayer.isPartyLeader()) {
                                for (GamePlayer gp : gamePlayer.getParty().getMember()) {
                                    log(gp, "リーダーにより、このPartyは解散されました。");
                                    gp.setParty(null);
                                }
                            } else {
                                log(player, "あなたはPartyのリーダーではありません!", "代わりに、/party leaveを使用してPartyを退出することができます。");
                            }
                        }
                        break;
                    case "leave":
                        if (gamePlayer.getParty() == null) {
                            log(player, "あなたはPartyに入っていません!");
                        } else {
                            if (gamePlayer.isPartyLeader()) {
                                GamePlayer NewLeader = Util.random(gamePlayer.getParty().getMember());
                                gamePlayer.getParty().setLeader(NewLeader);
                                for (GamePlayer gp : gamePlayer.getParty().getMember()) {
                                    log(gp, "リーダーがこのPartyから退出したため、" + NewLeader.getPlayer().getName() + "がリーダーになりました。");
                                }
                                gamePlayer.getParty().leader.getParty().member.remove(gamePlayer);
                                gamePlayer.setParty(null);
                            } else {
                                log(player, "Partyから退出しました。");
                                for (GamePlayer gp : gamePlayer.getParty().getMember()) {
                                    log(gp, player.getName() + "がPartyから退出しました。");
                                }
                                if (gamePlayer.getParty().getMember().isEmpty()) {
                                    log(gamePlayer.getParty().leader, "Partyから誰もいなくなったため、Partyが解散されました。");
                                    gamePlayer.getParty().leader.setParty(null);
                                }
                                gamePlayer.getParty().leader.getParty().member.remove(gamePlayer);
                                gamePlayer.setParty(null);
                            }
                        }
                        break;
                }
            case 2:
                switch (args[0].toLowerCase()) {
                    case "invite":
                        if (gamePlayer.getParty() == null) {
                            gamePlayer.setParty(new Party());
                            gamePlayer.getParty().member.add(gamePlayer);
                        }
                        for (GamePlayer gp : HideAndSeek.getINSTANCE().getGame().getPlayers().values()) {
                            if (gp.getPlayer().getName().equalsIgnoreCase(args[1])) {
                                if (gp.getParty() == gamePlayer.getParty()) {
                                    log(gamePlayer, "そのプレイヤーはこのPartyに参加しています!");
                                } else {
                                    for (GamePlayer pm : gamePlayer.getParty().getMember()) {
                                        log(pm, player.getName() + "が" + gp.getPlayer().getName() + "を招待しました!", "<red>60<yellow>秒以内なら入ることができます!");
                                    }
                                    log(gp, player.getName() +"があなたを彼らのPartyに招待しました! <red>60<yellow>秒以内なら入ることができます!");
                                    Party currentParty = gamePlayer.getParty();
                                    currentParty.getInvites().add(gp);

                                    Bukkit.getScheduler().runTaskLater(HideAndSeek.getINSTANCE(), () -> {
                                        if (gamePlayer.getParty() == currentParty) {
                                            if (!gamePlayer.getParty().getInvites().contains(gp)) {
                                                currentParty.invites.remove(gp);
                                                for (GamePlayer pm : gamePlayer.getParty().getMember()) {
                                                    log(pm, gp.getPlayer().getName() + "への招待期限が切れました!");
                                                }
                                            }
                                        }
                                    }, 1200L);
                                }
                            } else {
                                log(gamePlayer, "そのプレイヤーはオフラインです!");
                            }
                        }
                        break;
                    case "accept":
                        if (gamePlayer.getParty() == null) {
                            for (GamePlayer gp : HideAndSeek.getINSTANCE().getGame().getPlayers().values()) {
                                if (gp.getParty().invites.contains(gamePlayer) && gp.getPlayer().getName().equalsIgnoreCase(args[1])) {
                                    gp.getParty().invites.remove(gp);
                                    for (GamePlayer pm : gamePlayer.getParty().getMember()) {
                                        log(pm, gamePlayer.getPlayer().getName() + "がPartyに参加しました!");
                                    }
                                    gp.getParty().member.add(gamePlayer);
                                } else {
                                    log(gamePlayer, "その招待は無効または存在しません!");
                                }
                            }
                        } else {
                            log(gamePlayer, "あなたは既にPartyに入っています!");
                        }
                        break;
                    case "kick":
                        if (gamePlayer.getParty() == null) {
                            log(gamePlayer, "あなたはPartyに入っていません!");
                        } else {
                            if (gamePlayer.isPartyLeader()) {
                                for (GamePlayer gp : HideAndSeek.getINSTANCE().getGame().getPlayers().values()) {
                                    if (gp.getParty() == gamePlayer.getParty() && gp.getPlayer().getName().equalsIgnoreCase(args[1])) {
                                        for (GamePlayer pm : gamePlayer.getParty().getMember()) {
                                            log(pm, gamePlayer.getPlayer().getName() + "がPartyから追い出されました!!");
                                        }
                                        gamePlayer.getParty().member.remove(gp);
                                    } else {
                                        log(gamePlayer, "そのプレイヤーはこのPartyにいないか、存在しません!");
                                    }
                                }
                            } else {
                                log(gamePlayer, "あなたはこのPartyのリーダーではありません!");
                            }
                        }
                        break;
                    case "leave":
                    case "list":
                    case "disband":
                        log(player, helpMessage);
                        break;
                }
        }
        return false;
    }

    public String[] helpMessage = new String[]{
            "<green>Partyコマンド: ",
            "<yellow>/party accept username <aqua>- 招待を承認します。",
            "<yellow>/party invite username <aqua>- プレイヤーを招待します。",
            "<yellow>/party kick username <aqua>- プレイヤーを追い出します。",
            "<yellow>/party list <aqua>- 現在のPartyのメンバーを表示します。",
            "<yellow>/party leave <aqua>- 現在のPartyから退出します。",
            "<yellow>/party disband <aqua>- Partyを解散します。"
    };

    public void log(GamePlayer gamePlayer, String... string) {
        Player player = gamePlayer.getPlayer();
        Common.sendMessage(player, "<gold><strikethrough>                           ");
        for (String s : string) {
            Common.sendMessage(player, "<yellow>" + s);
        }
        Common.sendMessage(player, "<gold><strikethrough>                           ");
    }
    public void log(Player player, String... string) {
        Common.sendMessage(player, "<gold><strikethrough>                           ");
        for (String s : string) {
            Common.sendMessage(player, "<yellow>" + s);
        }
        Common.sendMessage(player, "<gold><strikethrough>                           ");
    }
}
