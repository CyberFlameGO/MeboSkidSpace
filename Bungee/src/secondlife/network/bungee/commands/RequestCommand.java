package secondlife.network.bungee.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import secondlife.network.bungee.handlers.RequestHandler;
import secondlife.network.bungee.handlers.SilentHandler;
import secondlife.network.bungee.utils.Color;
import secondlife.network.bungee.utils.StringUtils;

public class RequestCommand extends Command {
	
	public RequestCommand() {
		super("request", "", "helpop", "staffhelp");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(Color.translate("&cPlayer use only!"));
			return;
		}
		
		ProxiedPlayer player = (ProxiedPlayer) sender;
		ServerInfo server = player.getServer().getInfo();
			
		if(server.getName().equalsIgnoreCase("Hub")) {
			player.sendMessage(Color.translate("&cRequest is disabled on this server."));
			return;
		}
		
		if(args.length == 0) {
			player.sendMessage(Color.translate("&cUsage: /request <reason...>"));
		} else {
			if(RequestHandler.isActive(player)) {
				player.sendMessage(Color.translate("&cYou can't use this command for another &l" + StringUtils.formatMilisecondsToMinutes(RequestHandler.getMillisecondsLeft(player))));
			} else {
				
				RequestHandler.applyCooldown(player);
				StringBuilder message = new StringBuilder();
				
				for(int i = 0; i < args.length; i++) {
					message.append(args[i]).append(" ");
				}
				
				if(server.getName().equalsIgnoreCase("UHC-1") || server.getName().equalsIgnoreCase("UHC-2")) {
					if(message.toString().toLowerCase().contains("heal") && message.toString().toLowerCase().contains("time")) {
						player.sendMessage(Color.translate("&cYou can see heal time in /config"));
						return;
					}
					if(message.toString().toLowerCase().contains("heal") && message.toString().toLowerCase().contains("final")) {
						player.sendMessage(Color.translate("&cYou can see heal time in /config"));
						return;
					}
					if(message.toString().toLowerCase().contains("pvp") && message.toString().toLowerCase().contains("time")) {
						player.sendMessage(Color.translate("&cYou can see pvp time in /config"));
						return;
					}
					if(message.toString().toLowerCase().contains("shrink")) {
						player.sendMessage(Color.translate("&cYou can see border shirnk time in /config"));
						return;
					}
					if(message.toString().toLowerCase().contains("apple") && message.toString().toLowerCase().contains("rate")) {
						player.sendMessage(Color.translate("&cApple rate is 2%"));
						return;
					}
					if(message.toString().toLowerCase().contains("scenario") || message.toString().toLowerCase().contains("scenarios")) {
						player.sendMessage(Color.translate("&ccYou can see current scenarios by doing /scenarios"));
						return;
					}
					if(message.toString().toLowerCase().contains("shear") || message.toString().toLowerCase().contains("shears")) {
						player.sendMessage(Color.translate("&cShears are enabled, apple rate while using shears is 1%"));
						return;
					}
					if(message.toString().toLowerCase().contains("nether")) {
						player.sendMessage(Color.translate("&cYou can check if nether is enabled in /config"));
						return;
					}
					if(message.toString().toLowerCase().contains("cross")) {
						player.sendMessage(Color.translate("&cCross teaming is allowed in team games"));
						return;
					}
				}
				
				player.sendMessage(Color.translate("&aUspjesno si zatrazio pomoc i staff je obavijesten!"));
				
				for(ProxiedPlayer online : BungeeCord.getInstance().getPlayers()) {
					if(!SilentHandler.silent.contains(online.getUniqueId())) {
						if(online.hasPermission("secondlife.staff")) {
							online.sendMessage(Color.translate("&2[Request] &7[" + player.getServer().getInfo().getName().toUpperCase() + "] &a" + player.getName() + " &7requested staff help:"));
							online.sendMessage(Color.translate("&2Reason: &7" + message));
						}
					}
				}
			}
		}
	}
}