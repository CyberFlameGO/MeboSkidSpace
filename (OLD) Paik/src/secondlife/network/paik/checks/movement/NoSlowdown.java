package secondlife.network.paik.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class NoSlowdown {

	public static void handleNoSlowdownEating(Player player, PlayerStats stats) {
		if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.noslowdown.eating")) {
			if(ServerUtils.isServerLagging()
					|| player.isDead()
					|| player.getVehicle() != null
					|| player.getAllowFlight()
					|| player.getGameMode() == GameMode.CREATIVE
					|| player.getPing() > 350)
				return;

			if(stats.getNoslowFoodVl() > 2) {
				stats.setNoslowFoodVl(0);
				Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "NoSlowdown (Eating)", player.getPing(), Bukkit.spigot().getTPS()[0]));
			}

			if(player.isSprinting()) {
				stats.setNoslowFoodVl(stats.getNoslowFoodVl() + 1);
			} else {
				stats.setNoslowFoodVl(0);
			}
		}
	}

	public static void handleNoSlowdownShooting(Player player, PlayerStats stats) {
		if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.noslowdown.shooting")) {
			if(ServerUtils.isServerLagging()
					|| player.isDead()
					|| player.getVehicle() != null
					|| player.getAllowFlight()
					|| player.getGameMode() == GameMode.CREATIVE
					|| player.getPing() > 350)
				return;

			if(stats.getNoslowBowVl() > 2) {
				stats.setNoslowBowVl(0);
				Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "NoSlowdown (Shooting)", player.getPing(), Bukkit.spigot().getTPS()[0]));
			}

			if(player.isSprinting()) {
				stats.setNoslowBowVl(stats.getNoslowBowVl() + 1);
			} else {
				stats.setNoslowBowVl(0);
			}
		}
	}
}
