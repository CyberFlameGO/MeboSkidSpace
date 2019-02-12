package secondlife.network.vituz.punishments.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.ServerUtils;

/**
 * Created by Marko on 12.04.2018.
 */
public class KickCommand extends BaseCommand {

    public KickCommand(Vituz plugin) {
        super(plugin);

        this.command = "kick";
        this.permission = Permission.STAFF_PERMISSION;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(Color.translate("&cUsage: /kick <player> <reason> <-s>"));
        } else {
            boolean silent = false;

            Player target = Bukkit.getPlayer(args[0]);

            if(Msg.checkOffline(sender, args[0])) return;

            StringBuilder sb = new StringBuilder();

            for(int i = 1; i < args.length; ++i) {
                sb.append(args[i]).append(" ");
            }

            String reason = sb.toString().trim();

            if(!VituzAPI.canPunish(sender, args[0])) {
                sender.sendMessage(Color.translate("&cSorry but you can't punish " + args[0] + "!"));
                return;
            }

            if(reason.contains("-s") || reason.contains("-silent")) {
                silent = true;
            }

            target.kickPlayer(Color.translate("&cYou have been kicked for " + reason + "."));

            if(silent) {
                ServerUtils.bungeeBroadcast("&7(Silent) &a" + args[0] + " was kicked by " + sender.getName() + ".", Permission.STAFF_PERMISSION);
            } else {
                ServerUtils.bungeeBroadcast("&a" + args[0] + " was kicked by " + sender.getName() + ".");
            }
        }
    }
}
