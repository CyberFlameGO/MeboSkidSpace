package secondlife.network.meetuplobby.commands;

import org.bukkit.entity.Player;
import secondlife.network.meetuplobby.MeetupLobby;
import secondlife.network.meetuplobby.utilties.MeetupUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.StringUtils;
import secondlife.network.vituz.utilties.command.Command;
import secondlife.network.vituz.utilties.command.param.Parameter;

/**
 * Created by Marko on 24.07.2018.
 */
public class MeetupCommands {

    @Command(names = {"setspawn", "spawnset"}, permissionNode = "secondlife.op")
    public static void setSpawn(Player player) {
        MeetupLobby.getInstance().getConfig().set("spawn-location", StringUtils.stringifyLocation(player.getLocation()));
        MeetupLobby.getInstance().getConfig().save();
        player.sendMessage(Color.translate("&eYou have set spawn location."));
    }

    @Command(names = {"setstats", "statsset"}, permissionNode = "secondlife.op")
    public static void setStats(Player player, @Parameter(name = "name") String name, @Parameter(name = "mongoValue") String mongoValue, @Parameter(name = "amount") int amount) {
        MeetupUtils.setStats(mongoValue, name, amount);
        player.sendMessage(Color.translate("&eYou have set &d" + mongoValue + " &eof &d" + name + " &eto &d" + amount + "&e."));
    }
}
