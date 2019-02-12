package secondlife.network.paik.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.utils.CheatUtils;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class Reach {

	public static void handleReachCheck(Player player, PlayerStats stats, int entityID) {
		if(!ConfigFile.configuration.getBoolean("enabled")) return;
		if(!ConfigFile.configuration.getBoolean("checks.reach")) return;
		
		if(ServerUtils.isServerLagging()) return;
		
		if(player.isDead() 
			|| player.getVehicle() != null 
			|| player.getAllowFlight() 
			|| player.getGameMode() == GameMode.CREATIVE 
			|| player.getPing() > 300) 
			return;
		
		Entity hit = null;
		
		if(player.getWorld().getEntities().size() > 0) {
			for(Entity entity : player.getWorld().getEntities()) {
				if(entity.getEntityId() == entityID) {
					hit = entity;
				}
			}
		}
		
		if(hit == null) return;
		if(!(hit instanceof Player)) return;
		
		Player target = (Player) hit;
		
		if(target.isDead() 
			|| target.getVehicle() != null 
			|| target.getAllowFlight() 
			|| target.getGameMode() == GameMode.CREATIVE 
			|| target.getPing() > 300) 
			return;
		
		if(CheatUtils.direction(player) == CheatUtils.direction(target)) return;
		
		if(stats.getReachVL() > 9) {
			stats.setReachVL(0);
			Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Reach", player.getPing(), Bukkit.spigot().getTPS()[0]));
		}
		
		double reach = CheatUtils.getHorizontalDistance(player.getLocation(), target.getLocation()) - CheatUtils.getYDifference(player.getLocation(), target.getLocation());
		double maxReach = 3.4;
		double playerPing = player.getPing();
		double entityPing = target.getPing();
		double deltaXZ = stats.getDelta();
		double entityDeltaXZ = target.getVelocity().length() > 0 ? target.getVelocity().length() : 0;
		
		maxReach += (playerPing + entityPing) * 0.004;
		maxReach += (deltaXZ + entityDeltaXZ) * 0.9;
		maxReach += Math.abs(player.getEyeLocation().getYaw() - target.getLocation().getYaw()) * 0.004;
		
		if(reach > maxReach) {
			stats.setReachVL(stats.getReachVL() + 1);
		} else {
			if(stats.getReachVL() > 0) {
				stats.setReachVL(stats.getReachVL() - 1);
			}
		}
	}
}
