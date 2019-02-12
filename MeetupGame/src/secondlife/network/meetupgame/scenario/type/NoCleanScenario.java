package secondlife.network.meetupgame.scenario.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.vituz.utilties.Color;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Marko on 11.06.2018.
 */
public class NoCleanScenario extends Scenario implements Listener {
	
	public static HashMap<UUID, Long> noClean = new HashMap<UUID, Long>();

	public NoCleanScenario() {
		super("No Clean", Material.DIAMOND_SWORD, "When player dies he will be invincible for 30 seconds!");		
	}
	
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(EntityDeathEvent event) {    	
    	if(!(event.getEntity() instanceof Player)) return;

    	Player killer = event.getEntity().getKiller();
    	
    	if(killer == null) return;

    	applyCooldown(killer);
    	
    	killer.sendMessage(Color.translate("&a[No Clean] Your No Clean invincibility has been added."));
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    	if(event.isCancelled()) return;
    	    	
    	if(!(event.getEntity() instanceof Player)) return;
    	if(!(event.getDamager() instanceof Player)) return;
    	
    	Player target = (Player) event.getEntity();
    	Player damager = (Player) event.getDamager();
    	
    	if(isActive(target)) {
    		damager.sendMessage(Color.translate("&c[No Clean] That player has No Clean invincibility."));
    		event.setCancelled(true);
    		return;
    	}
     	
    	if(isActive(damager)) {
    		removeCooldown(damager);
    		damager.sendMessage(Color.translate("&c[No Clean] Your No Clean invincibility has been removed."));
    	}
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
    	if(event.isCancelled()) return;
    	
    	if(!(event.getEntity() instanceof Player)) return;
    	
    	Player player = (Player) event.getEntity();
    	
    	if(isActive(player)) event.setCancelled(true);
    }
    
    public static void applyCooldown(Player player) {
    	noClean.put(player.getUniqueId(), System.currentTimeMillis() + (30 * 1000));
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
