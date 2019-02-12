package secondlife.network.meetupgame.commands;

import org.bukkit.entity.Player;
import secondlife.network.meetupgame.data.MeetupData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.command.Command;
import secondlife.network.vituz.utilties.command.param.Parameter;

/**
 * Created by Marko on 23.07.2018.
 */
public class EloCommand {

    @Command(names = {"elo"})
    public static void handleUsage(Player player) {
        player.sendMessage(Color.translate("&eYour elo is &d" + MeetupData.getByName(player.getName()).getElo() + "&e."));
    }

    @Command(names = {"elo"})
    public static void handleView(Player player, @Parameter(name = "name") Player target) {
        player.sendMessage(Color.translate("&eElo of &d" + target.getName() + " &eis &d" + MeetupData.getByName(target.getName()).getElo() + "&e."));
    }
}
