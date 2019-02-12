package secondlife.network.paik.utils;

import org.bukkit.Bukkit;

public class ServerUtils {
	
	public static boolean isServerLagging() {
		return Bukkit.spigot().getTPS()[0] < 18.5;
	}
}
