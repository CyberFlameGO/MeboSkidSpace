package secondlife.network.paik.utils;

import org.bukkit.entity.Player;

public class LocationUtils {

	public static String getLocation(Player player) {
		return player.getWorld().getName() + ", " + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ();
	}
}
