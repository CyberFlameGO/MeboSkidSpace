package secondlife.network.victions.commands;

import org.bukkit.entity.Player;
import secondlife.network.victions.player.FactionsData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.command.Command;
import secondlife.network.vituz.utilties.command.param.Parameter;

/**
 * Created by Marko on 18.07.2018.
 */
public class BalanceCommand {

    @Command(names = {"balance", "bal"})
    public static void handleBalance(Player player) {
        FactionsData data = FactionsData.getByName(player.getName());
        player.sendMessage(Color.translate("&eYour balance is &d$" + data.getBalance() + "&e."));
    }

    @Command(names = {"balance ", "bal "})
    public static void handleBalancePlayer(Player player, @Parameter(name = "name") Player target) {
        FactionsData data = FactionsData.getByName(target.getName());
        player.sendMessage(Color.translate("&eBalance of &d" + target.getName() + " &eis &d$" + data.getBalance() + "&e."));
    }

    @Command(names = {"balance set", "balance give", "bal set", "bal give"}, permissionNode = "secondlife.op")
    public static void handleBalanceSet(Player player, @Parameter(name = "name") Player target, @Parameter(name = "amount") int amount) {
        if(amount > 100000) {
            player.sendMessage(Color.translate("&cBalance limit is 100000!"));
            return ;
        }

        FactionsData data = FactionsData.getByName(target.getName());

        data.setBalance(data.getBalance() + amount);

        player.sendMessage(Color.translate("&eYou have set balance of &d" + target.getName() + " &eto &d$" + data.getBalance() + "&e."));
        target.sendMessage(Color.translate("&eYour balance is now &d$" + data.getBalance() + "&e."));
    }
}
