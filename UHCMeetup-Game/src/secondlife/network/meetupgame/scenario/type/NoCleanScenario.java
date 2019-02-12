package secondlife.network.meetupgame.scenario.type;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.utilties.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NoCleanScenario extends Scenario implements Listener {
	
	private static Map<UUID, Long> noClean = new HashMap<>();

	public NoCleanScenario() {
		super("No Clean", Material.DIAMOND_SWORD, "When player dies he will be invincible for 30 seconds.");
	}

	public static void handleDeath(Entity entity, Player killer) {
		if(!(entity instanceof Player)) return;

		if(killer == null) return;

		applyCooldown(killer);

		VituzNametag.reloadPlayer(killer);
		VituzNametag.reloadOthersFor(killer);

		killer.sendMessage(Color.translate("&a[No Clean] Your No Clean invincibility has been added."));

		Bukkit.getScheduler().runTaskLater(MeetupGame.getInstance(), () -> {
			if(killer.isOnline()) {
				VituzNametag.reloadPlayer(killer);
				VituzNametag.reloadOthersFor(killer);
			}
		}, 20 * 30L);
	}

	public static void handleEntityDamageByEntity(Entity entity, Entity entityDamager, EntityDamageByEntityEvent event) {
		if(!(entity instanceof Player)) return;
		if(!(entityDamager instanceof Player)) return;

		Player target = (Player) entity;
		Player damager = (Player) entityDamager;

		if(isActive(target)) {
			damager.sendMessage(Color.translate("&c[No Clean] That player has No Clean invincibility."));
			event.setCancelled(true);
			return;
		}

		if(isActive(damager)) {
			removeCooldown(damager);
			VituzNametag.reloadPlayer(damager);
			VituzNametag.reloadOthersFor(damager);
			damager.sendMessage(Color.translate("&c[No Clean] Your No Clean invincibility has been removed."));
		}
	}

	public static void handleEntityDamage(Entity entity, EntityDamageEvent event) {
		if(!(entity instanceof Player)) return;

		Player player = (Player) entity;

		if(isActive(player)) {
			event.setCancelled(true);
		}
	}
    
    public static void applyCooldown(Player player) {
    	noClean.put(player.getUniqueId(), System.currentTimeMillis() + 30 * 1000);
    }
	
	public static boolean isActive(Player player) {
        return noClean.containsKey(player.getUniqueId()) && System.currentTimeMillis() < noClean.get(player.getUniqueId());
	}
	
	public static void removeCooldown(Player player) {
		noClean.remove(player.getUniqueId());
	}
	
	public static long getMillisecondsLeft(Player player) {
	    if(noClean.containsKey(player.getUniqueId())) {
	    	return Math.max(noClean.get(player.getUniqueId()) - System.currentTimeMillis(), 0L);
	    }
	    return 0L;
	}
}
