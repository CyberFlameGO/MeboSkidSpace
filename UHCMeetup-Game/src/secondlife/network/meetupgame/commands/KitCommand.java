package secondlife.network.meetupgame.commands;

import org.bukkit.entity.Player;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.command.Command;
import secondlife.network.vituz.utilties.command.param.Parameter;
import secondlife.network.vituz.utilties.inventory.InventoryUtils;

import java.io.IOException;

/**
 * Created by Marko on 23.07.2018.
 */
public class KitCommand {

    private static MeetupGame plugin = MeetupGame.getInstance();
    
    @Command(names = {"kit"}, permissionNode = "secondlife.op")
    public static void handleUsage(Player player) {
        player.sendMessage(Color.translate("&cUsage: /kit <view|set> <kit>"));
    }

    @Command(names = {"kit view"}, permissionNode = "secondlife.op")
    public static void handleView(Player player, @Parameter(name = "kit") int number) {
        if(number < 0) {
            player.sendMessage(Color.translate("&cInteger must be positive."));
            return;
        }

        if(!plugin.getKits().contains(number + ".inventory")) {
            player.sendMessage(Color.translate("&cThat kit doesn't exists!"));
            return;
        }

        try {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            String items = plugin.getKits().getString(plugin.getKitsManager().getKits().get(plugin.getKitsManager().getCount()) + ".inventory");
            String armor = plugin.getKits().getString(plugin.getKitsManager().getKits().get(plugin.getKitsManager().getCount()) + ".armor");

            player.getInventory().setContents(InventoryUtils.fromBase64(items).getContents());
            player.getInventory().setArmorContents(InventoryUtils.itemStackArrayFromBase64(armor));
            player.updateInventory();

            player.sendMessage(Color.translate("&eYou are now viewing &dKit " + number + "&e."));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Command(names = {"kit set"}, permissionNode = "secondlife.op")
    public static void handleSet(Player player, @Parameter(name = "kit") int number) {
        if(number < 0) {
            player.sendMessage(Color.translate("&cInteger must be positive."));
            return;
        }

        plugin.getKits().set(number + ".inventory", InventoryUtils.toBase64(player.getInventory()));
        plugin.getKits().set(number + ".armor", InventoryUtils.itemStackArrayToBase64(player.getInventory().getArmorContents()));
        plugin.getKits().save();

        player.sendMessage(Color.translate("&eYou have set &dKit " + number + "&e."));
    }
}
