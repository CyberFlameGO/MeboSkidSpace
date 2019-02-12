package secondlife.network.bungee.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import secondlife.network.bungee.handlers.ReportHandler;
import secondlife.network.bungee.handlers.RequestHandler;
import secondlife.network.bungee.handlers.SilentHandler;
import secondlife.network.bungee.utils.Color;
import secondlife.network.bungee.utils.StringUtils;

public class ReportCommand extends Command {
	
	public ReportCommand() {
		super("report", "", "hacker");
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
			player.sendMessage(Color.translate("&cReport is disabled on this server."));
			return;
		}
		
		if(args.length < 2) {
			player.sendMessage(Color.translate("&cUsage: /report <player> <reason>"));
		} else {
		    if(ReportHandler.isActive(player)) {
				player.sendMessage(Color.translate("&cYou can't use this command for another &l" + StringUtils.formatMilisecondsToMinutes(ReportHandler.getMillisecondsLeft(player))));
			} else {
				
				ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

				if(target == null) {
					sender.sendMessage(Color.translate("&cThat player is not online!"));
					return;
				}

				ServerInfo serverTarget = target.getServer().getInfo();
				
				if(serverTarget == null) {
					sender.sendMessage(Color.translate("&cThat player is not online!"));
					return;
				}
				
				if(target == player) {
					player.sendMessage(Color.translate("&cYou can't report yourself!"));
					return;
				}
				
				ReportHandler.applyCooldown(player);
				StringBuilder message = new StringBuilder();
				
				for(int i = 1; i < args.length; i++) {
					message.append(args[i]).append(" ");
				}

				player.sendMessage(Color.translate("&aYou have reported &c" + target.getName() + " &aand all staff is now alerted!"));
				
				for(ProxiedPlayer online : BungeeCord.getInstance().getPlayers()) {
					if(online.hasPermission("secondlife.staff")) {
						if(!SilentHandler.silent.contains(online.getUniqueId())) {
							online.sendMessage(Color.translate("&4[Report] &7[" + serverTarget.getName().toUpperCase() + "] &c" + player.getName() + " &7reported player named &c&l" + target.getName() + "&7!"));
							online.sendMessage(Color.translate("&4Reason: &7" + message));
							//online.sendMessage(Color.translate("&8(&9Report&8) &8(&9" + serverTarget.getName().toUpperCase() + "&8) &9" + player.getName() + " &bhas reported &c" + target.getName() + " &bfor&7: &f" + message));
						}
					}
				}
			}
		}
	}
}