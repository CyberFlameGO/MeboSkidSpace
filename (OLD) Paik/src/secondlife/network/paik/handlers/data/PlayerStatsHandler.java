package secondlife.network.paik.handlers.data;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import secondlife.network.paik.Paik;
import secondlife.network.paik.utils.Handler;

public class PlayerStatsHandler extends Handler implements Listener {

	public static HashMap<UUID, PlayerStats> statsMap;

	public PlayerStatsHandler(Paik plugin) {
		super(plugin);
		
		statsMap = new HashMap<UUID, PlayerStats>();
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			addStats(player);
		}
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		addStats(player);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		destroyStats(player);
	}
	
	public static void addStats(Player player) {
		statsMap.put(player.getUniqueId(), new PlayerStats(player));
	}

	public static void destroyStats(Player player) {
		statsMap.remove(player.getUniqueId());
	}

	public static PlayerStats getStats(Player player) {
		return statsMap.get(player.getUniqueId());
	}
}
