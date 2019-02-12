package secondlife.network.hub.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import secondlife.network.hub.Hub;
import secondlife.network.hub.utilties.Manager;

import java.util.Random;

public class MultiSpawnManager extends Manager {

	public MultiSpawnManager(Hub plugin) {
		super(plugin);
	}

	public void handleMove(Player player, Location from, Location to) {
        if(from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ() || from.getPitch() != to.getPitch() || from.getYaw() != to.getYaw()) return;
        
        if(to.getBlockY() <= -15 && !player.isDead()) {
        	randomSpawn(player);
        }
    }

    public void randomSpawn(Player player) {
        if(plugin.getUtilities().getConfigurationSection("SPAWNS") == null) {
            return;
        }
        
        int size = plugin.getUtilities().getConfigurationSection("SPAWNS").getKeys(false).size();
        
        if(size == 0) return;
        
        int ii = randInt(0, size - 1);
        int i = 0;
        
        for(String key : plugin.getUtilities().getConfigurationSection("SPAWNS").getKeys(false)) {
            if(i == ii) {
                int lX = plugin.getUtilities().getInt("SPAWNS." + key + ".X");
                int lY = plugin.getUtilities().getInt("SPAWNS." + key + ".Y");
                int lZ = plugin.getUtilities().getInt("SPAWNS." + key + ".Z");
                
                String lWorld = plugin.getUtilities().getString("SPAWNS." + key + ".WORLD");

                Location location;

                location = player.getLocation();
                location.setX(lX);
                location.setY(lY);
                location.setZ(lZ);
                
                if(Bukkit.getWorld(lWorld) != null) {
                    location.setWorld(Bukkit.getWorld(lWorld));
                }

                player.teleport(location);
                break;
            }
            i++;
        }
    }

     private int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

}
