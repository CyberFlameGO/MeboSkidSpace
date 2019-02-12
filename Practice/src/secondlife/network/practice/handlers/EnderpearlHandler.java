package secondlife.network.practice.handlers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import secondlife.network.practice.Practice;
import secondlife.network.practice.match.Match;
import secondlife.network.practice.match.MatchState;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.utilties.Handler;
import secondlife.network.vituz.handlers.FreezeHandler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderpearlHandler extends Handler implements Listener {

	public static Map<UUID, Long> cooldown = new HashMap<>();
	
	public EnderpearlHandler(Practice plugin) {
		super(plugin);

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
    
	public static void disable() { 
    	cooldown.clear();
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	Action action = event.getAction();
    	
    	if(player.getGameMode().equals(GameMode.CREATIVE)) return;

    	if(event.hasItem() && (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) && event.getItem().getType().equals(Material.ENDER_PEARL)) {
			PracticeData data = PracticeData.getByName(player.getName());

			if(data.getPlayerState().equals(PlayerState.FIGHTING)) {
				Match match = this.plugin.getMatchManager().getMatch(data);

				if(match.getMatchState() == MatchState.STARTING) {
					event.setCancelled(true);
					player.sendMessage(CC.RED + "You can't throw pearls right now!");
					player.updateInventory();
					return;
				}
			}

    		if(isActive(player)) {
    			event.setUseItemInHand(Event.Result.DENY);
    			player.sendMessage(Color.translate("&cYou can't use this for another &l" + StringUtils.getRemaining(getMillisecondsLeft(player), true) + "!"));
    		} else {
    			if(!FreezeHandler.freezed.contains(player.getUniqueId())) {
    				applyCooldown(player);
    			}
    		}
    	}
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
    	Player player = event.getEntity();
    	
    	if(!cooldown.containsKey(player.getUniqueId())) return;
    	
		cooldown.remove(player.getUniqueId());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
    	Player player = event.getPlayer();
    	
    	if(!cooldown.containsKey(player.getUniqueId())) return;
		
		cooldown.remove(player.getUniqueId());
    }
    
    public static boolean isActive(Player player) {
        return cooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis() < cooldown.get(player.getUniqueId());
    }
    
    public static void applyCooldown(Player player) {
    	cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (15 * 1000));
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
