package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

/**
 * Created by Marko on 10.04.2018.
 */
public class ProtocolCommand extends BaseCommand {

    public ProtocolCommand(Vituz plugin) {
        super(plugin);

        this.command = "protocol";
        this.permission = Permission.STAFF_PERMISSION;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(Color.translate("&cUsage: /protocol <player|list>"));
        } else {
            if(args[0].equalsIgnoreCase("list")) {
                int first = 0;
                int second = 0;
                int third = 0;

                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(ProtocolSupportAPI.getProtocolVersion(player) == ProtocolVersion.MINECRAFT_1_7_5) {
                        first++;
                    } else if(ProtocolSupportAPI.getProtocolVersion(player) == ProtocolVersion.MINECRAFT_1_7_10) {
                        second++;
                    } else if(ProtocolSupportAPI.getProtocolVersion(player) == ProtocolVersion.MINECRAFT_1_8) {
                        third++;
                    }
                }

                sender.sendMessage(Color.translate("&7[&5&l1.7.5&7] &d-> &d" + first + (first == 1 ? " user" : " users")));
                sender.sendMessage(Color.translate("&7[&5&l1.7.10&7] &d-> &d" + second  + (second == 1 ? " user" : " users")));
                sender.sendMessage(Color.translate("&7[&5&l1.8&7] &d-> &d" + third  + (third == 1 ? " user" : " users")));
            } else {
                Player target = Bukkit.getPlayer(args[0]);

                if(Msg.checkOffline(sender, args[0])) return;

                sender.sendMessage(Color.translate("&d" + args[0] + " &dis using version &7(&d" + VituzAPI.getVersion(target) + "&7)"));
            }
        }
    }
}
