package secondlife.network.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import secondlife.network.bungee.Bungee;
import secondlife.network.bungee.utils.Color;

/**
 * Created by Marko on 09.04.2018.
 */
public class MotdCommand extends Command {

    public MotdCommand() {
        super("motd", "secondlife.op", "setmotd");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(Color.translate("&cUsage: /motd <text> - {nl} new line"));
        } else {
            StringBuilder message = new StringBuilder();

            for(int i = 0; i < args.length; i++) {
                message.append(args[i]).append(" ");
            }

            Bungee.configuration.set("server_motd", message.toString());
            Bungee.getInstance().saveConfig();

            sender.sendMessage(Color.translate("&eYou have updated motd to: " + Color.translate(message.toString())));
        }
    }
}
