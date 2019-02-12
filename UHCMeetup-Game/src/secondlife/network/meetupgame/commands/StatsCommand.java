package secondlife.network.meetupgame.commands;

import org.bukkit.entity.Player;
import secondlife.network.meetupgame.data.MeetupData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.command.Command;
import secondlife.network.vituz.utilties.command.param.Parameter;

/**
 * Created by Marko on 23.07.2018.
 */
public class StatsCommand {

    @Command(names = {"stats"})
    public static void handleUsage(Player player) {
        MeetupData data = MeetupData.getByName(player.getName());

        player.sendMessage(Color.translate("&eYour stats:"));
        player.sendMessage(Color.translate("&eWins: &d" + data.getWins()));
        player.sendMessage(Color.translate("&ePlayed: &d" + data.getPlayed()));
        player.sendMessage(Color.translate("&eKills: &d" + data.getKills()));
        player.sendMessage(Color.translate("&eDeaths: &d" + data.getDeaths()));
        player.sendMessage(Color.translate("&eKD: &d" + data.getKD()));
        player.sendMessage(Color.translate("&eKill Streak: &d" + data.getHighestKillStreak()));
        player.sendMessage(Color.translate("&eRerolls: &d" + data.getRerolls()));
    }

    @Command(names = {"stats"})
    public static void handleView(Player player, @Parameter(name = "name") Player target) {
        MeetupData data = MeetupData.getByName(target.getName());

        player.sendMessage(Color.translate("&eStats of &d" + target.getName() + "&e!"));
        player.sendMessage(Color.translate("&eWins: &d" + data.getWins()));
        player.sendMessage(Color.translate("&ePlayed: &d" + data.getPlayed()));
        player.sendMessage(Color.translate("&eKills: &d" + data.getKills()));
        player.sendMessage(Color.translate("&eDeaths: &d" + data.getDeaths()));
        player.sendMessage(Color.translate("&eKD: &d" + data.getKD()));
        player.sendMessage(Color.translate("&eKill Streak: &d" + data.getHighestKillStreak()));
        player.sendMessage(Color.translate("&eRerolls: &d" + data.getRerolls()));
    }
}
