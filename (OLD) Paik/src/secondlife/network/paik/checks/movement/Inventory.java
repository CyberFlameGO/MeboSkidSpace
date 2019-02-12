package secondlife.network.paik.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.paik.Paik;
import secondlife.network.paik.handlers.CheatHandler;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.handlers.events.PlayerMoveByBlockEvent;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class Inventory {

	public static void handleInventoryClose(Player player, PlayerStats stats) {
		stats.setInventoryOpen(false);
		stats.setMovesWhileInventoryClosed(0);
		stats.setHitsWhileInventoryOpen(0);

		new BukkitRunnable() {
			public void run() {
				if(stats.isInventoryOpen()) {
					stats.setInventoryOpen(false);
					stats.setMovesWhileInventoryClosed(0);
					stats.setHitsWhileInventoryOpen(0);
				}
			}
		}.runTaskLaterAsynchronously(Paik.getInstance(), 2L);
	}

	public static void handleInventoryMove(Player player, PlayerStats stats, PlayerMoveByBlockEvent event) {
		if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.inventory.move")) {
			if(player.getPing() > 250
					|| player.getAllowFlight()
					|| player.getGameMode() == GameMode.CREATIVE)
				return;

			if(CheatHandler.ignore.containsKey(player.getUniqueId()) && System.currentTimeMillis() < CheatHandler.ignore.get(player.getUniqueId())) return;

			if(System.currentTimeMillis() - stats.getJoined() < 1500) return;

			if(stats.getMovesWhileInventoryClosed() > 10) {
				stats.setMovesWhileInventoryClosed(0);
				Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Inventory Move", player.getPing(), Bukkit.spigot().getTPS()[0]));
			}

			if(!stats.isInventoryOpen()) return;

			Location from = event.getFrom().clone();
			Location to = event.getTo().clone();

			from.setY(0.0D);
			to.setY(0.0D);

			Double distance = Double.valueOf(from.distance(to));

			if(distance.doubleValue() > 0.25) {
				stats.setMovesWhileInventoryClosed(stats.getMovesWhileInventoryClosed() + 1);
			}
		}
	}

	public static void handleAutoPotion(Player player, PlayerStats stats) {
		if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.inventory.autopotion")) {
			if(ServerUtils.isServerLagging()) return;

			if(player.getGameMode() != GameMode.SURVIVAL) return;

			if(stats.getPotionsSplashedWhileInventoryOpen() > 1) {
				stats.setPotionsSplashedWhileInventoryOpen(0);
				Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "AutoPotion", player.getPing(), Bukkit.spigot().getTPS()[0]));
			}

			if(!stats.isInventoryOpen()) return;

			stats.setPotionsSplashedWhileInventoryOpen(stats.getPotionsSplashedWhileInventoryOpen() + 1);
		}
	}
}
 