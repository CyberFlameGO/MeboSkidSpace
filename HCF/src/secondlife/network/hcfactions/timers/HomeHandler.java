package secondlife.network.hcfactions.timers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.FactionManager;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeHandler extends Handler implements Listener {

	public static Map<UUID, Location> destinationMap = new HashMap<UUID, Location>();
	public static HashMap<UUID, Long> cooldown;

	public HomeHandler(HCF plugin) {
		super(plugin);

		cooldown = new HashMap<UUID, Long>();

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public static void handleMove(Player player, Location from, Location to) {
		if(!isActive(player)) return;

		if(from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) return;

		cancelTeleport(player, Color.translate("&cYou moved a block, therefore cancelling your teleport."));
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();

		if (!(event.getEntity() instanceof Player)) return;

		cancelTeleport((Player) entity, Color.translate("&cYou took damage, teleportation cancelled!"));
	}


	public static int getNearbyEnemies(Player player, int distance) {
		FactionManager factionManager = RegisterHandler.getInstancee().getFactionManager();
		Faction playerFaction = factionManager.getPlayerFaction(player.getName());

		int count = 0;

		Collection<Entity> nearby = player.getNearbyEntities(distance, distance, distance);
		for (Entity entity : nearby) {
			if (entity instanceof Player) {
				Player target = (Player) entity;

				if (!target.canSee(player) || !player.canSee(target)) continue;

				if (playerFaction == null || factionManager.getPlayerFaction(target) != playerFaction) count++;
			}
		}

		return count;
	}

	public static boolean teleport(Player player, Location location, int seconds, TeleportCause cause) {
		cancelTeleport(player, null);

		boolean result;

		if (seconds <= 0) {
			result = player.teleport(location, cause);
			stopCooldown(player);
		} else {
			destinationMap.put(player.getUniqueId(), location.clone());
			applyCooldown(player, seconds);

			result = true;

			new BukkitRunnable() {
				public void run() {
					Location destination = destinationMap.remove(player.getUniqueId());

					if(destination != null) {
						if(isActive(player)) {
							destination.getChunk();

							player.teleport(destination, TeleportCause.COMMAND);
						}
					}
				}
			}.runTaskLater(HCF.getInstance(), (seconds - 1) * 20);
		}

		return result;
	}

	public static  void cancelTeleport(Player player, String reason) {
		if (isActive(player)) {
			stopCooldown(player);

			if (reason != null && !reason.isEmpty()) {
				player.sendMessage(reason);
			}
		}
	}

	public static Location getDestination(Player player) {
		return destinationMap.get(player.getUniqueId());
	}

	public static boolean isActive(Player player) {
		return cooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis() < cooldown.get(player.getUniqueId());
	}

	public static void applyCooldown(Player player, int seconds) {
		cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000));
	}

	public static void stopCooldown(Player player) {
		cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (0 * 1000));
	}

	public static long getMillisecondsLeft(Player player) {
		if (cooldown.containsKey(player.getUniqueId())) {
			return Math.max(cooldown.get(player.getUniqueId()) - System.currentTimeMillis(), 0L);
		}

		return 0L;
	}
}
