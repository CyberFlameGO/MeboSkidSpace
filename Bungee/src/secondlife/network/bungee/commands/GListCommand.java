package secondlife.network.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;
import secondlife.network.bungee.utils.Color;

public class GListCommand extends Command {
	
	public GListCommand() {
		super("glist", "", "gplayers", "gp", "players", "sllist", "slglist");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 1) {
			if(!args[0].equalsIgnoreCase("all")) {
				sender.sendMessage(Color.translate("&cUsage: /glist or /glist all"));
				return;
			}
			
			for(ServerInfo server : ProxyServer.getInstance().getServers().values()) {
				sender.sendMessage(Color.translate("&9[&b" + server.getName() + "&9] &6- &c" + server.getPlayers().size()));
			}
			sender.sendMessage(Color.translate("&aTotal online &6- &c" + ProxyServer.getInstance().getOnlineCount()));
			return;
		}
		
		for(ServerInfo server : ProxyServer.getInstance().getServers().values()) {
			
			if(server.getPlayers().size() != 0) {
				sender.sendMessage(Color.translate("&9[&b" + server.getName() + "&9] &6- &c" + server.getPlayers().size()));
			}
		}
		sender.sendMessage(Color.translate("&aTotal online &6- &c" + ProxyServer.getInstance().getOnlineCount()));
	}
}