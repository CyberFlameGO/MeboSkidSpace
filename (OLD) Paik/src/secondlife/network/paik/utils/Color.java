package secondlife.network.paik.utils;

import org.bukkit.ChatColor;

public class Color {
	
	public static String translate(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
}
