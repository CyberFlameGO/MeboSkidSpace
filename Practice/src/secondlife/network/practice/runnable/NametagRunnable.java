package secondlife.network.practice.runnable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import secondlife.network.practice.Practice;
import secondlife.network.practice.match.Match;
import secondlife.network.practice.match.MatchTeam;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.vituz.providers.scoreboard.VituzScoreboard;
import secondlife.network.vituz.utilties.Color;

import java.util.UUID;

/**
 * Created by Marko on 19.07.2018.
 */
public class NametagRunnable extends BukkitRunnable {

    public NametagRunnable() {
        runTaskTimerAsynchronously(Practice.getInstance(), 40L, 60L);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            handleScoreboardChanges(player, VituzScoreboard.getBoard());
        });
    }

    private void handleScoreboardChanges(Player player, Scoreboard scoreboard) {
        Team red = scoreboard.getTeam("red");

        if (red == null) {
            red = scoreboard.registerNewTeam("red");
        }
        Team green = scoreboard.getTeam("green");

        if (green == null) {
            green = scoreboard.registerNewTeam("green");
        }

        red.setPrefix(Color.translate("&c"));
        green.setPrefix(Color.translate("&c"));

        PracticeData playerData = PracticeData.getByKurac(player.getUniqueId());

        if (playerData.getPlayerState() != PlayerState.FIGHTING) {
            for (String entry : red.getEntries()) {
                red.removeEntry(entry);
            }
            for (String entry : green.getEntries()) {
                green.removeEntry(entry);
            }
            return;
        }

        Match match = Practice.getInstance().getMatchManager().getMatch(player.getUniqueId());

        for (MatchTeam team : match.getTeams()) {
            for (UUID teamUUID : team.getAlivePlayers()) {
                Player teamPlayer = Bukkit.getPlayer(teamUUID);
                if (teamPlayer != null) {
                    String teamPlayerName = teamPlayer.getName();
                    if (team.getTeamID() == playerData.getTeamID() && !match.isFFA()) {
                        if (!green.hasEntry(teamPlayerName)) {
                            green.addEntry(teamPlayerName);
                        }
                    } else {
                        if (!red.hasEntry(teamPlayerName)) {
                            red.addEntry(teamPlayerName);
                        }
                    }
                }
            }
        }
    }
}
