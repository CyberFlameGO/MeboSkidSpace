package secondlife.network.victions.commands;

import org.bukkit.entity.Player;
import secondlife.network.victions.player.FactionsData;
import secondlife.network.victions.utilities.CustomLocation;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.command.Command;
import secondlife.network.vituz.utilties.command.param.Parameter;

/**
 * Created by Marko on 18.07.2018.
 */
public class HomeCommand {

    @Command(names = {"home"})
    public static void handleHomeUsage(Player player) {
        player.sendMessage(Color.translate("&cUsage: /home <list> <name>"));
    }

    @Command(names = {"home list"})
    public static void handleHomeList(Player player) {
        FactionsData data = FactionsData.getByName(player.getName());

        if(data.getHomes().size() > 0) {
            StringBuilder builder = new StringBuilder();

            data.getHomes().keySet().forEach(home -> {
                if(builder.length() > 0) {
                    builder.append("&f, ");
                }

                builder.append("&d").append(home);
            });

            player.sendMessage(Color.translate(builder.toString()));
        } else {
            player.sendMessage(Color.translate("&eYou don't have any homes set yet."));
        }
    }

    @Command(names = {"home"})
    public static void handleHomeGo(Player player, @Parameter(name = "home") String homeName) {
        FactionsData data = FactionsData.getByName(player.getName());

        data.getHomes().forEach((home, location) -> {
            if(!home.contains(homeName.toLowerCase())) {
                player.sendMessage(Color.translate("&cHome '" + homeName.toLowerCase() + "' doesn't exist."));
                return;
            }

            data.applyHomeCooldown(player, location.toBukkitLocation());
        });
    }

    @Command(names = {"sethome"})
    public static void handleSetHome(Player player, @Parameter(name = "home") String homeName) {
        FactionsData data = FactionsData.getByName(player.getName());

        if(data.getHomes().size() > data.getHomeLimit()) {
            player.sendMessage(Color.translate("&cYour home limit is &l" + data.getHomeLimit() + "&c"));
            return;
        }

        data.getHomes().forEach((home, location) -> {
            if(home.contains(homeName.toLowerCase())) {
                player.sendMessage(Color.translate("&cHome '" + homeName.toLowerCase() + "' already exist."));
                return;
            }

            data.getHomes().put(homeName, CustomLocation.fromBukkitLocation(player.getLocation()));
            player.sendMessage(Color.translate("&eYou have set your &d" + homeName + " &ehome."));
        });
    }

    @Command(names = {"deletehome", "delhome"})
    public static void handleDeleteHome(Player player, @Parameter(name = "home") String homeName) {
        FactionsData data = FactionsData.getByName(player.getName());

        data.getHomes().forEach((home, location) -> {
            if(!home.contains(homeName.toLowerCase())) {
                player.sendMessage(Color.translate("&cHome '" + homeName.toLowerCase() + "' doesn't exist."));
                return;
            }

            data.getHomes().remove(homeName);
            player.sendMessage(Color.translate("&eYou have deleted your &d" + homeName + " &ehome."));
        });
    }
}
