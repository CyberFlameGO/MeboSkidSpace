package secondlife.network.paik.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import secondlife.network.paik.handlers.CheatHandler;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class FastEat {

	public static void handleFastEat(Player player, PlayerStats stats) {
		if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.fasteat")) {
			if(ServerUtils.isServerLagging()
					|| player.isDead()
					|| player.getVehicle() != null
					|| player.getAllowFlight()
					|| player.getGameMode() == GameMode.CREATIVE
					|| player.getPing() > 350)
				return;;

			if(stats.getFastEatVL() > 4) {
				stats.setFastEatVL(0);
				Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "FastEat", player.getPing(), Bukkit.spigot().getTPS()[0]));

				if(!ConfigFile.configuration.getBoolean("autobans") || player.hasPermission("secondlife.staff")) return;

				CheatHandler.handleBan(player);
			}

			if(System.currentTimeMillis() - stats.getLastEat() < 1000) {
				stats.setFastEatVL(stats.getFastEatVL() + 1);
			} else {
				if(stats.getFastEatVL() > 0) {
					stats.setFastEatVL(stats.getFastEatVL() - 1);
				}
			}

			stats.setLastEat(System.currentTimeMillis());
		}
	}
}
