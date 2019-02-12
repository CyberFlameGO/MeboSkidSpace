package secondlife.network.bungee.handlers;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import secondlife.network.bungee.Bungee;
import secondlife.network.bungee.utils.Handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RequestHandler extends Handler implements Listener {

	public static Map<UUID, Long> requestCooldowns = new HashMap<>();
	
	public RequestHandler(Bungee plugin) {
		super(plugin);
	}
	
	public static void disable() {
		requestCooldowns.clear();
	}
	
	public static void applyCooldown(ProxiedPlayer player) {
    	requestCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (180 * 1000));
    }
	
	public static boolean isActive(ProxiedPlayer player) {
        return requestCooldowns.containsKey(player.getUniqueId()) && System.currentTimeMillis() < requestCooldowns.get(player.getUniqueId());
    }
	
	public static long getMillisecondsLeft(ProxiedPlayer player) {
	    if(requestCooldowns.containsKey(player.getUniqueId())) {
	    	return Math.max(requestCooldowns.get(player.getUniqueId()) - System.currentTimeMillis(), 0L);
	    }
	    return 0L;
	}
}