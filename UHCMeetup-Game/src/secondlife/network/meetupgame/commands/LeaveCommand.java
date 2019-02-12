package secondlife.network.meetupgame.commands;

import org.bukkit.entity.Player;
import secondlife.network.vituz.utilties.ServerUtils;
import secondlife.network.vituz.utilties.command.Command;

/**
 * Created by Marko on 23.07.2018.
 */
public class LeaveCommand {

    @Command(names = {"leave"})
    public static void handleLeave(Player player) {
        ServerUtils.sendToServer(player, "UHCMeetup-Lobby");
    }
}
