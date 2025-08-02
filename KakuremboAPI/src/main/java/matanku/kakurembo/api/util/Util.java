package matanku.kakurembo.api.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Util {

    public static List<Integer> generateNumbers(int amount, int start, int end) {
        List<Integer> list = new ArrayList<>();
        for(int i = start; i <= end; i++){
            list.add(i);
        }
        Collections.shuffle(list);
        return list.subList(0, amount);
    }

    public static void hideNameTags(Player player) {
        String teamName = "API";
        //gets the players scoreboard and creates a new one if it does not exist
        Scoreboard scoreboard = player.getScoreboard();
        //loops through all online players
        for (Player all : Bukkit.getOnlinePlayers()) {
            //gets all the players scoreboard and creates a new one if it does not exist
            Scoreboard s = all.getScoreboard();

            //creates a new team with the players name if it does not exist.
            Team team = s.getTeam(teamName);
            if (team == null) team = s.registerNewTeam(teamName);

            //this is where the name changing comes in. changing the prefix of the team shows up above the players nametag
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            if (!team.hasEntry(player.getName())) {
                team.addEntry(player.getName());
            }
            all.setScoreboard(s); //sets every players' scoreboard, adding "player"'s name to the tablist and above their character

            if (all != player) continue; //Checks if all is not equal to the player so player can receive team prefixes of other players in their own tablist

            Team t = scoreboard.getTeam(teamName);
            if (t == null) t = scoreboard.registerNewTeam(teamName);

            t.addPlayer(all);
        }
    }

}
