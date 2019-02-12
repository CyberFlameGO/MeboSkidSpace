package secondlife.network.meetupgame.commands;

import org.bukkit.entity.Player;
import secondlife.network.meetupgame.data.GameData;
import secondlife.network.meetupgame.managers.GameManager;
import secondlife.network.meetupgame.states.GameState;
import secondlife.network.meetupgame.utilties.MeetupUtils;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.Tasks;
import secondlife.network.vituz.utilties.command.Command;

/**
 * Created by Marko on 23.07.2018.
 */
public class AnnounceCommand {

    @Command(names = {"announce"})
    public static void handleAnnounce(Player player) {
        if(!player.hasPermission(Permission.DONOR_PERMISSION) || !player.hasPermission(Permission.STAFF_PERMISSION)) {
            player.sendMessage(Msg.NO_PERMISSION);
            return;
        }

        GameData data = GameManager.getGameData();

        if(data.getGameState().equals(GameState.PLAYING)) {
            player.sendMessage(Color.translate("&cGame is already started."));
            return;
        }

        if(data.isCanAnnounce()) {
            player.sendMessage(Color.translate("&cSomeone already announced this game. Please try again later."));
            return;
        }

        MeetupUtils.announce(player, VituzAPI.getServerName());
        data.setCanAnnounce(true);

        Tasks.runLater(() -> data.setCanAnnounce(false), 20 * 15);
    }
}
