package secondlife.network.hcfactions.timers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.StringUtils;

public class AppleHandler extends Handler implements Listener {

	public static HashMap<UUID, Long> cooldown;
	
	public AppleHandler(HCF plugin) {
		super(plugin);
		
		cooldown = new HashMap<UUID, Long>();
		
		Bukkit.getPluginManager().registerEvents(this, this.getInstance());
	}
	
	public static void disable() { 
    	cooldown.clear();
    }
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		
		if(item.getType().equals(Material.GOLDEN_APPLE) && item.getDurability() == 0) {
			if(isActive(player)) {
				event.setCancelled(true);
				
				player.sendMessage(Color.translate("&cYou can't use this item for another &l" + StringUtils.getRemaining((int) getMillisecondsLeft(player), false) + "&c."));
			} else {
				applyCooldown(player);
			}
		}
	}
	
	public static boolean isActive(Player player) {
		return cooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis() < cooldown.get(player.getUniqueId());
	}

	public static void applyCooldown(Player player) {
		cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (5 * 1000));
	}

	public static long getMillisecondsLeft(Player player) {
		if(cooldown.containsKey(player.getUniqueId())) {
			return Math.max(cooldown.get(player.getUniqueId()) - System.currentTimeMillis(), 0L);
		}
		
		return 0L;
	}
}
