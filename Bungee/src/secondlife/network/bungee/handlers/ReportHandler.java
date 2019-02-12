package secondlife.network.bungee.handlers;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import secondlife.network.bungee.Bungee;
import secondlife.network.bungee.utils.Handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReportHandler extends Handler implements Listener {

	public static Map<UUID, Long> reportCooldowns = new HashMap<>();
	
	public ReportHandler(Bungee plugin) {
		super(plugin);
	}
	
	public static void disable() {
		reportCooldowns.clear();
	}
	
	public static void applyCooldown(ProxiedPlayer player) {
		reportCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (180 * 1000));
    }
	
	public static boolean isActive(ProxiedPlayer player) {
        return reportCooldowns.containsKey(player.getUniqueId()) && System.currentTimeMillis() < reportCooldowns.get(player.getUniqueId());
    }
	
	public static long getMillisecondsLeft(ProxiedPlayer player) {
	    if(reportCooldowns.containsKey(player.getUniqueId())) {
	    	return Math.max(reportCooldowns.get(player.getUniqueId()) - System.currentTimeMillis(), 0L);
	    }
	    return 0L;
	}
}