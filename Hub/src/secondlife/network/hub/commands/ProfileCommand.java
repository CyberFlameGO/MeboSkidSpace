package secondlife.network.hub.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import secondlife.network.hub.utilties.profile.BukkitProfileUtils;
import secondlife.network.hub.utilties.profile.RemoveThread;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.command.Command;
import secondlife.network.vituz.utilties.command.param.Parameter;

/**
 * Created by Marko on 12.04.2018.
 */
public class ProfileCommand {

    @Command(names = {"deleteprofile", "dp"}, permissionNode = "secondlife.op")
    public static void handleDelete(CommandSender sender, @Parameter(name = "player") OfflinePlayer target) {
        sender.sendMessage(Color.translate("&eDeleting &d" + target.getName() + "'s &eprofile!"));
        BukkitProfileUtils.getByPlayerFile(target).delete();
        sender.sendMessage(Color.translate("&eProfile deleted!"));
    }

    @Command(names = {"deleteprofiles", "dps"}, permissionNode = "secondlife.op")
    public static void handleDelete(CommandSender sender) {
        sender.sendMessage(Color.translate("&eDeleting duplicated profiles!"));
        new RemoveThread().start();
        sender.sendMessage(Color.translate("&aProfiles deleted!"));
    }

    @Command(names = {"toggleprofile", "tprofile"}, permissionNode = "secondlife.op")
    public static void handleEnableDisable(CommandSender sender) {
        BukkitProfileUtils.setEnabled(!BukkitProfileUtils.isEnabled());
        sender.sendMessage(Color.translate("&eYou have " + (BukkitProfileUtils.isEnabled() ? "&aEnabled" : "&cDisabled") + " &eplayer name check."));
    }
}
