package secondlife.network.hcfactions.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;

public class BorderHandler extends Handler implements Listener {
		
	public BorderHandler(HCF plugin) {
		super(plugin);
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onCreaturePreSpawn(CreatureSpawnEvent event) {
		if(isWithinBorder(event.getLocation())) return;
		
		event.setCancelled(true);
	}

	@EventHandler
	public void onBucketEmpty(PlayerBucketFillEvent event) {
		if(isWithinBorder(event.getBlockClicked().getLocation())) return;
		
		event.setCancelled(true);
		
		Player player = event.getPlayer();
		
		player.sendMessage(Color.translate("&cYou can't fill buckets past the border."));
	}

	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		if(isWithinBorder(event.getBlockClicked().getLocation())) return;
		
		event.setCancelled(true);
		
		Player player = event.getPlayer();
		
		player.sendMessage(Color.translate("&cYou can't empty buckets past the border."));
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(isWithinBorder(event.getBlock().getLocation())) return;
		
		event.setCancelled(true);
		
		Player player = event.getPlayer();
		
		player.sendMessage(Color.translate("&cYou can't place blocks past the border."));
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(isWithinBorder(event.getBlock().getLocation())) return;
		
		event.setCancelled(true);
		
		Player player = event.getPlayer();
		
		player.sendMessage(Color.translate("&cYou can't break blocks past the border."));
	}

	public static void handleMove(Player player, Location from, Location to) {
		if(from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) return;

		if(isWithinBorder(to)) return;
		if(!isWithinBorder(from)) return;

		player.sendMessage(Color.translate("&cYou can't go past the border."));

		player.teleport(from);

		Entity vehicle = player.getVehicle();

		if(vehicle == null) return;

		vehicle.eject();
		vehicle.teleport(from);
		vehicle.setPassenger(player);
	}

	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event) {
		Location to = event.getTo();
		if(to == null) return;
		if(isWithinBorder(to)) return;
		
		Player player = event.getPlayer();
		
		TeleportCause cause = event.getCause();

		if(cause != TeleportCause.NETHER_PORTAL || (cause == TeleportCause.ENDER_PEARL && isWithinBorder(event.getFrom()))) {
			event.setCancelled(true);

			player.sendMessage(Color.translate("&cYou can't go past the border."));
		} else {
			World.Environment toEnvironment = to.getWorld().getEnvironment();

			if(toEnvironment != Environment.NORMAL) return;

			int x = to.getBlockX();
			int z = to.getBlockZ();
			int borderSize = HCFConfiguration.bordersizes.get(toEnvironment);

			boolean extended = false;

			if(Math.abs(x) > borderSize) {
				to.setX((x > 0) ? (borderSize - 50) : (-borderSize + 50));

				extended = true;
			}

			if(Math.abs(z) > borderSize) {
				to.setZ((z > 0) ? (borderSize - 50) : (-borderSize + 50));

				extended = true;
			}

			if(extended) {
				to.add(0.5, 0.0, 0.5);

				event.setTo(to);

				player.sendMessage(Color.translate("&cThis portals travel location was over the border. It has been moved inwards."));
			}
		}
	}
	
	public static boolean isWithinBorder(Location location) {
		int borderSize = HCFConfiguration.bordersizes.get(location.getWorld().getEnvironment());
		
		return Math.abs(location.getBlockX()) <= borderSize && Math.abs(location.getBlockZ()) <= borderSize;
	}
}