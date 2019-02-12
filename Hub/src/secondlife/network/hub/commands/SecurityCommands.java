package secondlife.network.hub.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hub.Hub;
import secondlife.network.hub.data.StaffData;
import secondlife.network.overpass.utilties.OverpassUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.command.Command;
import secondlife.network.vituz.utilties.command.param.Parameter;

/**
 * Created by Marko on 22.07.2018.
 */
public class SecurityCommands {

    @Command(names = {"auth", "security"}, permissionNode = "secondlife.staff")
    public static void handleAuth(Player player, @Parameter(name = "password") String password) {
        StaffData data = StaffData.getByName(player.getName());

        if(data.getPassword().equalsIgnoreCase("")) {
            player.sendMessage(Color.translate("&cYou are not registered!"));
            return;
        }

        if(!password.equals(data.getPassword())) {
            player.sendMessage(Color.translate("&cWrong password"));
            return;
        }

        player.sendMessage(Color.translate("&7(Staff) &eYou have logged in!"));
        Hub.getInstance().getStaffSecurityManager().getUsers().remove(player.getUniqueId());
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

        OverpassUtils.sendAuthToBungee(player);
    }

    @Command(names = {"securityregister"}, permissionNode = "secondlife.staff")
    public static void handleRegister(Player player, @Parameter(name = "password") String password) {
        StaffData data = StaffData.getByName(player.getName());

        if(!data.getPassword().equalsIgnoreCase("")) {
            player.sendMessage(Color.translate("&cYou are already registered!"));
            return;
        }

        data.setPassword(password);
        player.kickPlayer(Color.translate("&cYou have set your password, please join back and login using /auth or /security"));
        Hub.getInstance().getStaffSecurityManager().getUsers().remove(player.getUniqueId());
    }

    @Command(names = {"securityreset"}, permissionNode = "secondlife.op")
    public static void handleReset(CommandSender sender, @Parameter(name = "name") String name) {
        StaffData data;
        Player target = Bukkit.getPlayer(name);

        if(target == null) {
            data = StaffData.getByName(name);
        } else {
            data = StaffData.getByName(target.getName());
        }

        if(data.getPassword().equalsIgnoreCase("")) {
            sender.sendMessage(Color.translate("&cThat player doesn't have security password!"));
            return;
        }

        data.setPassword("");
        sender.sendMessage(Color.translate("&eSuccesfully reset!"));

        if(target != null) {
            target.kickPlayer(Color.translate("&cYour security password was reset, please join back and set it again!"));
        } else {
            data.save();
        }
    }
}
