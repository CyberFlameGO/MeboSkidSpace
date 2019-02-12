package secondlife.network.victions.commands;

import org.bukkit.entity.Player;
import secondlife.network.victions.player.FactionsData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.command.Command;

/**
 * Created by Marko on 18.07.2018.
 */
public class LogoutCommand {

    @Command(names = {"logout"})
    public static void handleLogout(Player player) {
        FactionsData data = FactionsData.getByName(player.getName());

        if(data.isLogoutActive(player)) {
            player.sendMessage(Color.translate("&cYou are already logging out."));
            return;
        }

        data.applyLogoutCooldown(player);
        player.sendMessage(Color.translate("&e&lLogging out... &ePlease wait &c30 &eseconds."));
    }
}
