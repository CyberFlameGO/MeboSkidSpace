package secondlife.network.bungee.utils;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Message {

	public static void sendMessage(String message) {
		for(ProxiedPlayer online : BungeeCord.getInstance().getPlayers()) {
			online.sendMessage(Color.translate(message));
		}
	}
	
	public static void sendMessage(String message, String permission) {
		for(ProxiedPlayer online : BungeeCord.getInstance().getPlayers()) {
			
			if(online.hasPermission(permission)) {
				online.sendMessage(Color.translate(message));
			}
		}
	}
}
