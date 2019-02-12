package secondlife.network.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import secondlife.network.bungee.antibot.BotBoth;
import secondlife.network.bungee.handlers.AntiBotHandler;
import secondlife.network.bungee.utils.Color;

public class ABVisualCommand extends Command {

	public ABVisualCommand() {
		super("antibotvisual",  "secondlife.op");
	}

	public void execute(CommandSender sender, String[] args) {
		if(!sender.hasPermission("secondlife.op")) {
			sender.sendMessage(Color.translate("&cNo permission."));
			return;
		}

		if(args.length == 0) {
			sender.sendMessage(Color.translate("&eThis server is running &dSecondLife Network &eantibot system!"));
		} else {
			if(args[0].equalsIgnoreCase("reload")) {
				
				BotBoth.attacks.clear();
				BotBoth.pings.clear();
				
				try {
					AntiBotHandler.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(AntiBotHandler.f);
				} catch (Exception e) {
					e.printStackTrace();
				}

				AntiBotHandler.limit = 3;
				BotBoth.timeout = 7;
			} else if(args[0].equalsIgnoreCase("on")) {
				
				if(AntiBotHandler.ignore.contains(sender)) {
					AntiBotHandler.ignore.remove(sender);
					
					sender.sendMessage(Color.translate("&fAntiBot has been turned &aON&f!"));
				} else {
					sender.sendMessage(Color.translate("&fAntiBot is already turned &aON&f!"));
				}
			} else if(args[0].equalsIgnoreCase("off")) {
				
				if(!AntiBotHandler.ignore.contains(sender)) {
					AntiBotHandler.ignore.add((ProxiedPlayer) sender);
					
					sender.sendMessage(Color.translate("&fAntiBot has been turned &cOFF&f!"));
				} else {
					sender.sendMessage(Color.translate("&fAntiBot is already turned &cOFF&f!"));
				}
			}
		}
	}
}
