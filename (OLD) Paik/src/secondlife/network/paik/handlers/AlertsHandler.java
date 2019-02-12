package secondlife.network.paik.handlers;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import secondlife.network.paik.Paik;
import secondlife.network.paik.utils.Handler;

public class AlertsHandler extends Handler implements Listener {
	
	public static HashMap<UUID, Map.Entry<Long, String>> delays;
	
	public AlertsHandler(Paik plugin) {
		super(plugin);
		
		delays = new HashMap<UUID, Map.Entry<Long, String>>();
	}
	
	public static void applyCooldown(Player player, String check) {
		delays.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Long, String>(System.currentTimeMillis() + 1000, check));
    }
	
	public static boolean isActive(Player player, String check) {
		return delays.containsKey(player.getUniqueId()) && System.currentTimeMillis() < delays.get(player.getUniqueId()).getKey().longValue() && check == delays.get(player.getUniqueId()).getValue();
    }
}
