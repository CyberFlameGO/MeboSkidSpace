package secondlife.network.bungee.handlers;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import secondlife.network.bungee.Bungee;
import secondlife.network.bungee.utils.Color;
import secondlife.network.bungee.utils.Handler;
import secondlife.network.bungee.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class AnnounceHandler extends Handler implements Listener {

	public static Map<String, Long> cooldowns = new HashMap<>();
	
	public AnnounceHandler(Bungee plugin) {
		super(plugin);

		ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
	}
	
	public static void disable() { cooldowns.clear(); }
	
	public static void applyCooldown(String server) {
		cooldowns.put(server, System.currentTimeMillis() + (20 * 1000));
    }
	
	public static boolean isActive(String  server) {
        return cooldowns.containsKey(server) && System.currentTimeMillis() < cooldowns.get(server);
    }
	
    public static long getMillisecondsLeft(String server) {
    	if(cooldowns.containsKey(server)) {
    		return Math.max(cooldowns.get(server) - System.currentTimeMillis(), 0L);
    	}
    	return 0L;
    }
    
    public static void handle(ProxiedPlayer player, String server) {
    	if(isActive(server)) {
			player.sendMessage(Color.translate("&cYou can't use this command for another &l" + StringUtils.formatMilisecondsToMinutes(getMillisecondsLeft(server))));
    		return;
    	}
    	
    	applyCooldown(server);
    	player.sendMessage(Color.translate("&eYou have announced this meetup!"));
    }
}
