package secondlife.network.paik.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class Message {

	public static String COMMANDS_FOR_PLAYER_USE_ONLY = Color.translate("&cSorry. For Player use only!");
	public static String COMMANDS_NO_PERMISSION_MESSAGE = Color.translate("&cNo Permission!");
	
	public static void sendMessage(String message) {
		for(Player online : Bukkit.getOnlinePlayers()) {
			online.sendMessage(message);
		}
	}
	
	public static void sendMessage(String message, String permission) {
		for(Player online : Bukkit.getOnlinePlayers()) {
			if(online.hasPermission(permission)) {
				online.sendMessage(message);
			}
		}
	}
}