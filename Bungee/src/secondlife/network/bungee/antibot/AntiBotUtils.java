package secondlife.network.bungee.antibot;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import secondlife.network.bungee.handlers.AntiBotHandler;
import secondlife.network.bungee.utils.Color;

public class AntiBotUtils {
	
	public static void sendMessage(String message) {
		for(ProxiedPlayer staff : ProxyServer.getInstance().getPlayers()) {
			if(staff.hasPermission("secondlife.op") && !AntiBotHandler.ignore.contains(staff)) {
				staff.sendMessage(Color.translate(message));
			}
		}
	}
}
