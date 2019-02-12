package secondlife.network.paik.utils;

import org.bukkit.entity.Player;

public class PlayerUtils {
	
	public static boolean isPlayerBypassing(Player player) {
		if(player.hasPermission("secondlife.staff")) {
			return true;
		}
		return false;
	}
}
