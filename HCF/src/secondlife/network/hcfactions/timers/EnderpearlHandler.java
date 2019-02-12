package secondlife.network.hcfactions.timers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.StringUtils;

import java.util.HashMap;
import java.util.UUID;

public class EnderpearlHandler extends Handler implements Listener {

	public static HashMap<UUID, Long> cooldown;
	
	public EnderpearlHandler(HCF plugin) {
		super(plugin);
		
		cooldown = new HashMap<UUID, Long>();
		
		Bukkit.getPluginManager().registerEvents(this, getInstance());
	}
    
	public static void disable() { 
    	cooldown.clear();
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	
    	if(player.getGameMode() == GameMode.CREATIVE) return;
    	
    	if(event.hasItem() && event.getItem().getType() == Material.ENDER_PEARL) {
    		if(isActive(player)) {
    			event.setUseItemInHand(Event.Result.DENY);
    			player.sendMessage(Color.translate("&cYou can't use this for another &l" + StringUtils.getRemaining(getMillisecondsLeft(player), true) + "!"));
    		}
    	}
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
    	Player player = event.getEntity();
    	
    	if(!cooldown.containsKey(player.getUniqueId())) return;
    	
		cooldown.remove(player.getUniqueId());
    }
    
    public static boolean isActive(Player player) {
        return cooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis() < cooldown.get(player.getUniqueId());
    }
    
    public static void applyCooldown(Player player) {
    	cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (16 * 1000));
    }

	public static void stopCooldown(Player player) {
		cooldown.remove(player.getUniqueId());
	}
    
    public static long getMillisecondsLeft(Player player) {
    	if(cooldown.containsKey(player.getUniqueId())) {
    		return Math.max(cooldown.get(player.getUniqueId()) - System.currentTimeMillis(), 0L);
    	}
    	
    	return 0L;
    }
}
