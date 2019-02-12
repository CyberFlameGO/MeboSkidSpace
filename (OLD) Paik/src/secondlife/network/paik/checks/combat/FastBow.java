package secondlife.network.paik.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import secondlife.network.paik.handlers.CheatHandler;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class FastBow {

	public static void handleFastBowInteract(Player player, PlayerStats stats) {
		if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.fastbow")) {
			if(ServerUtils.isServerLagging()
					|| player.getItemInHand() == null
					|| player.getItemInHand().getType() != Material.BOW
					|| player.getAllowFlight()
					|| player.isDead()) return;

			stats.setPull(System.currentTimeMillis());
		}
	}

	public static void handleFastBowShoot(Player player, PlayerStats stats, double power) {
		if(ServerUtils.isServerLagging() || player.getPing() > 200) return;
		
		if(stats.getBowVL() > 7) {
        	stats.setBowVL(0);
        	Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "FastBow", player.getPing(), Bukkit.spigot().getTPS()[0]));
        	
        	if(!ConfigFile.configuration.getBoolean("autobans") || player.hasPermission("secondlife.staff")) return;
        	
        	CheatHandler.handleBan(player);
        }
		
		Long time = System.currentTimeMillis() - stats.getPull();
        Long timeLimit = 300L;
        
        if(power > 2.5 && time < timeLimit) {
        	stats.setBowVL(stats.getBowVL() + 1);
        } else {
        	if(stats.getBowVL() > 0) {
        		stats.setBowVL(stats.getBowVL() - 1);
        	}
        }
        
        stats.setPull(0);
	}
}
