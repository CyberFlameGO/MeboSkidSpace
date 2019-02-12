package secondlife.network.paik.checks.movement.fly;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import secondlife.network.paik.handlers.CheatHandler;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.handlers.data.PlayerStatsHandler;
import secondlife.network.paik.handlers.events.PlayerMoveByBlockEvent;
import secondlife.network.paik.utils.CheatUtils;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class FlyB {

	public static void handleFly(Player player, PlayerStats stats, PlayerMoveByBlockEvent event) {
		if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.flyB")) {
			if(ServerUtils.isServerLagging()) {
				if(!CheatHandler.ignore.isEmpty()) {
					CheatHandler.ignore.clear();
				}
				return;
			}

			if(player.getAllowFlight()
					|| player.getVehicle() != null
					|| !CheatUtils.isInAir(player)
					|| CheatUtils.blocksNear(player))
				return;

			if(CheatHandler.ignore.containsKey(player.getUniqueId()) && System.currentTimeMillis() < CheatHandler.ignore.get(player.getUniqueId())) return;

			Location from = event.getFrom().clone();
			Location to = event.getTo().clone();

			if(from.getY() == to.getY() && from.getX() != to.getX() && from.getZ() != to.getZ()) {

				if(player.getPing() > 250) {
					stats.setFlyBVL(stats.getFlyBVL() + 1);
				} else {
					stats.setFlyBVL(stats.getFlyBVL() + 3);
				}

				if(stats.getFlyBVL() > 15) {
					stats.setFlyBVL(0);
					Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Fly B", player.getPing(), Bukkit.spigot().getTPS()[0]));
				}
			}
		}
	}
	
	public static void removeOne(Player player) {
		PlayerStats stats = PlayerStatsHandler.getStats(player);
		
		if(stats.getFlyBVL() > 0) {
			stats.setFlyBVL(stats.getFlyBVL() - 1);
		}
	}
}