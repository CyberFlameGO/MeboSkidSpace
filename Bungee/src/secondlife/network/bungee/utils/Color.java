package secondlife.network.bungee.utils;

import net.md_5.bungee.api.ChatColor;

public class Color {

	public static String translate(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
}
