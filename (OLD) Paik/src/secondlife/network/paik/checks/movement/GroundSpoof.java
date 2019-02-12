package secondlife.network.paik.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import secondlife.network.paik.handlers.CheatHandler;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.handlers.events.PlayerMoveByBlockEvent;
import secondlife.network.paik.utils.CheatUtils;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class GroundSpoof {

	public static void handleGroundSpoof(Player player, PlayerStats stats, PlayerMoveByBlockEvent event) {
		if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.groundspoof")) {
			if(ServerUtils.isServerLagging()) return;

			if(player.getGameMode() == GameMode.CREATIVE
					|| player.getAllowFlight()
					|| player.getVehicle() != null
					|| player.getPing() > 200
					|| !player.isOnGround()
					|| !CheatUtils.isInAir(player)
					|| CheatUtils.blocksNear(player))
				return;

			Location a = player.getLocation().subtract(0.0D, 1.0D, 0.0D);

			if(CheatUtils.blocksNear(a) || CheatHandler.ignore.containsKey(player.getUniqueId()) && System.currentTimeMillis() < CheatHandler.ignore.get(player.getUniqueId())) return;

			if(stats.getNofallVL() > 10) {
				stats.setNofallVL(0);
				Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "GroundSpoof", player.getPing(), Bukkit.spigot().getTPS()[0]));
			}

			if(player.getFallDistance() == 0.0 && event.getFrom().getY() > event.getTo().getY()) {
				stats.setNofallVL(stats.getNofallVL() + 1);
			} else {
				stats.setNofallVL(0);
			}
		}
	}
}
