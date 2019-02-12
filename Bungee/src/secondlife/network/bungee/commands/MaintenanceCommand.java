package secondlife.network.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import secondlife.network.bungee.Bungee;
import secondlife.network.bungee.utils.Color;

/**
 * Created by Marko on 09.04.2018.
 */
public class MaintenanceCommand extends Command {

    public MaintenanceCommand() {
        super("maintenance", "secondlife.op", "globalwl", "globalwhitelist");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(Color.translate("&cUsage: /maintenance <on|off>"));
        } else {
            if(args[0].equalsIgnoreCase("on")) {
                Bungee.configuration.set("whitelisted", true);
                Bungee.getInstance().saveConfig();

                sender.sendMessage(Color.translate("&aYou have enabled maintenance mode!"));
            } else if(args[0].equalsIgnoreCase("off")) {
                Bungee.configuration.set("whitelisted", false);
                Bungee.getInstance().saveConfig();

                sender.sendMessage(Color.translate("&cYou have disabled maintenance mode!"));
            }
        }
    }
}
