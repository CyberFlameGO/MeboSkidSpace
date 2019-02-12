package secondlife.network.hcfactions.timers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LogoutHandler extends Handler implements Listener {

	public static HashMap<UUID, LogoutTask> logoutTasks;
	public static HashMap<UUID, Long> warmup;
	public static ConcurrentHashMap<Player, Integer> teleporting;
	
	public LogoutHandler(HCF plugin) {
		super(plugin);
		
		logoutTasks = new HashMap<UUID, LogoutTask>();
		warmup = new HashMap<UUID, Long>();
		teleporting = new ConcurrentHashMap<>();
		
		Bukkit.getPluginManager().registerEvents(this, getInstance());
	}
	
	public static void disable() {
		logoutTasks.clear();
		warmup.clear();
	}

	public static void handleMove(Player player, Location from, Location to) {
		if(from.getPitch() != to.getPitch() || from.getYaw() != to.getYaw()) return;
		if(logoutTasks.containsKey(player.getUniqueId())) {
			if(from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
				logoutTasks.get(player.getUniqueId()).cancel();
				logoutTasks.remove(player.getUniqueId());

				if(teleporting.containsKey(player)) {
					int runnable = teleporting.get(player);

					Bukkit.getScheduler().cancelTask(runnable);
					teleporting.remove(player);

					player.sendMessage(Color.translate("&e&lLOGOUT &c&lCANCELLED!"));
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {		
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			
			if(logoutTasks.containsKey(player.getUniqueId())) {
				logoutTasks.get(player.getUniqueId()).cancel();
				logoutTasks.remove(player.getUniqueId());
			}
			
			if (teleporting.containsKey(player)) {
				int runnable = teleporting.get(player);
				Bukkit.getScheduler().cancelTask(runnable);
				teleporting.remove(player);
				
				if(logoutTasks.containsKey(player.getUniqueId())) {
					logoutTasks.get(player.getUniqueId()).cancel();
					logoutTasks.remove(player.getUniqueId());
				}
				
				player.sendMessage(Color.translate("&e&lLOGOUT &c&lCANCELLED!"));
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if(logoutTasks.containsKey(player.getUniqueId())) {
			logoutTasks.get(player.getUniqueId()).cancel();
			logoutTasks.remove(player.getUniqueId());
		}
		
		if(teleporting.containsKey(player)) {
			int runnable = teleporting.get(player);
			
			Bukkit.getScheduler().cancelTask(runnable);
			
			teleporting.remove(player);
			
			if(logoutTasks.containsKey(player.getUniqueId())) {
				logoutTasks.get(player.getUniqueId()).cancel();
				logoutTasks.remove(player.getUniqueId());
			}
			
			player.sendMessage(Color.translate("&e&lLOGOUT &c&lCANCELLED!"));
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerKickEvent event) {
		Player player = event.getPlayer();

		if(logoutTasks.containsKey(player.getUniqueId())) {
			logoutTasks.get(player.getUniqueId()).cancel();
			logoutTasks.remove(player.getUniqueId());
		}
		
		if(teleporting.containsKey(player)) {
			int runnable = teleporting.get(player);
			
			Bukkit.getScheduler().cancelTask(runnable);
			
			teleporting.remove(player);
		}
	}
	
	public static void applyWarmup(Player player) {
    	warmup.put(player.getUniqueId(), System.currentTimeMillis() + (30 * 1000));
    }
	
	public static boolean isActive(Player player) {
        return warmup.containsKey(player.getUniqueId()) && System.currentTimeMillis() < warmup.get(player.getUniqueId());
    }
	
	public static long getMillisecondsLeft(Player player) {
	    if(warmup.containsKey(player.getUniqueId())) {
	    	return Math.max(warmup.get(player.getUniqueId()) - System.currentTimeMillis(), 0L);
	    }
	    return 0L;
	}
	
	public static void createLogout(Player player) {
		LogoutTask logoutTask = new LogoutTask(player);
		logoutTask.runTaskLater(HCF.getInstance(), 30 * 20);
		
		applyWarmup(player);
		
		logoutTasks.put(player.getUniqueId(), logoutTask);
	}
	
	public static void removeLogout(Player player) {
		if(logoutTasks.containsKey(player.getUniqueId())) {
			logoutTasks.get(player.getUniqueId()).cancel();
			logoutTasks.remove(player.getUniqueId());
		}
	}

	public ConcurrentHashMap<Player, Integer> getTeleporting() {
		return teleporting;
	}

	public void setTeleporting(ConcurrentHashMap<Player, Integer> teleporting) {
		LogoutHandler.teleporting = teleporting;
	}

	public static class LogoutTask extends BukkitRunnable {
		
		private Player player;
		
		public LogoutTask(Player player) {
			this.player = player;
		}

		@Override
		public void run() {
			player.setMetadata("LogoutCommand", new FixedMetadataValue(HCF.getInstance(), Boolean.TRUE));
			player.kickPlayer(Color.translate("&cYou have been safely logged out from the server!"));
			
			logoutTasks.remove(player.getUniqueId());
		}
	}
}
