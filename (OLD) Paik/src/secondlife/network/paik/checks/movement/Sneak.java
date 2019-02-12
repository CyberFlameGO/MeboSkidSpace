package secondlife.network.paik.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class Sneak {

	public static void handleSneak(Player player, PlayerStats stats) {
		if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.sneak")) {
			if(ServerUtils.isServerLagging()
					|| player.isDead()
					|| System.currentTimeMillis() - stats.getLastBlockPlace() < 1000
					|| System.currentTimeMillis() - stats.getJoined() < 1500)
				return;

			if(stats.getSneakVL() > 50) {
				stats.setSneakVL(0);
				Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Sneak", player.getPing(), Bukkit.spigot().getTPS()[0]));
			}

			if(player.isSneaking()) {
				if(stats.getSneak1() != 0) {
					stats.setSneak2(System.currentTimeMillis());
				}

				if(stats.getSneak1() == 0) {
					stats.setSneak1(System.currentTimeMillis());
				}

				if(stats.getSneak1() == 0 || stats.getSneak2() == 0) return;

				long diff = (stats.getSneak2() - stats.getSneak1());

				if(diff < 150) {
					stats.setSneakVL(stats.getSneakVL() + 1);
				} else {
					stats.setSneakVL(0);
				}

				stats.setSneak1(0);
				stats.setSneak2(0);
			}
		}
	}
}
