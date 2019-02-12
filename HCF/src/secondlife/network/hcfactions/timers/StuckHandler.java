package secondlife.network.hcfactions.timers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.FactionManager;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.hcfactions.utilties.file.UtilitiesFile;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StuckHandler extends Handler implements Listener {

	private static Map<UUID, Location> startedLocations = new HashMap<UUID, Location>();
	public static HashMap<UUID, Long> cooldown;

	public StuckHandler(HCF plugin) {
		super(plugin);

		cooldown = new HashMap<UUID, Long>();

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public static void handleMove(Player player, Location to) {
		if(!isActive(player)) return;

		Location from = startedLocations.get(player.getUniqueId());

		checkMovement(player, from, to);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();

		if(!isActive(event.getPlayer())) return;

		Location from = this.startedLocations.get(player.getUniqueId());
			
		checkMovement(player, from, event.getTo());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(isActive(event.getPlayer())) {
			stopCooldown(event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		
		Player player = (Player) event.getEntity();

		if(isActive(player)) {
			player.sendMessage(Color.translate("&cYou took damage, teleportation cancelled!"));
			
			stopCooldown(player);
		}
	}

	public static void teleport(UUID userUUID) {
		Player player = Bukkit.getPlayer(userUUID);

		if(player == null) return;
		
		FactionManager factionManager = RegisterHandler.getInstancee().getFactionManager();
		PlayerFaction faction = factionManager.getPlayerFaction(player);
		Location loc = null;
		
		Location spawn = StringUtils.destringifyLocation(UtilitiesFile.configuration.getString("World-Spawn.world-spawn"));
		
		if(faction == null) {
			loc = spawn.clone().add(0.5, 0.0, 0.5);
		} else {
			if(faction.getHome() == null) {
				loc = spawn.clone().add(0.5, 0.0, 0.5);
			} else {
				Location home = StringUtils.destringifyLocation(faction.getHome());

				loc = home;
			}
		}

		if (player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
			player.sendMessage(Color.translate("&cYou have been teleported to the nearest safe area."));
		}
	}

	private static void checkMovement(Player player, Location from, Location to) {
		if(isActive(player)) {
			if(from == null) {
				stopCooldown(player);
				return;
			}

			int xDiff = Math.abs(from.getBlockX() - to.getBlockX());
			int yDiff = Math.abs(from.getBlockY() - to.getBlockY());
			int zDiff = Math.abs(from.getBlockZ() - to.getBlockZ());

			if((xDiff > 5) || (yDiff > 5) || (zDiff > 5)) {
				stopCooldown(player);

				player.sendMessage(Color.translate("&cYou moved more than 5.0 blocks, teleport cancelled!"));
			}
		}
	}

	public static boolean isActive(Player player) {
		return cooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis() < cooldown.get(player.getUniqueId());
	}

	public static void applyCooldown(Player player) {
		startedLocations.put(player.getUniqueId(), player.getLocation());

		cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (60 * 1000));

		new BukkitRunnable() {
			public void run() {
				if(isActive(player)) {
					teleport(player.getUniqueId());
				}
			}
		}.runTaskLater(HCF.getInstance(), 1180L);
	}

	public static void stopCooldown(Player player) {
		cooldown.remove(player.getUniqueId());
		startedLocations.remove(player.getUniqueId());
	}

	public static long getMillisecondsLeft(Player player) {
		if(cooldown.containsKey(player.getUniqueId())) {
			return Math.max(cooldown.get(player.getUniqueId()) - System.currentTimeMillis(), 0L);
		}

		return 0L;
	}
}