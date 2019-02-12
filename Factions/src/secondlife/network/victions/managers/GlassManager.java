package secondlife.network.victions.managers;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import secondlife.network.victions.Victions;
import secondlife.network.victions.VictionsAPI;
import secondlife.network.victions.player.FactionsData;
import secondlife.network.victions.utilities.Manager;

import java.util.*;

public class GlassManager extends Manager implements Listener {

	private Map<UUID, List<Location>> locationsWorldGuard = new HashMap<>();
	
	public GlassManager(Victions plugin) {
		super(plugin);
	}

	public void handleMove(Player player, Location from, Location to) {
        if(plugin.getPlayerManager().isSpawnTagActive(player)) {
            for(ProtectedRegion protectedRegion : VictionsAPI.getWorldGuard().getRegionManager(from.getWorld()).getRegions().values()) {
                if(protectedRegion != null && protectedRegion.getFlag(DefaultFlag.PVP) == StateFlag.State.DENY) {
                    if(protectedRegion.contains(to.getBlockX(), to.getBlockY(), to.getBlockZ())) {
                        to = from;
                    }

                    renderGlassWorldGuard(player, to, protectedRegion);
                }
            }
        } else {
            this.removeGlass(player);
        }
    }
	
	private void renderGlassWorldGuard(Player player, Location location, ProtectedRegion protectedRegion) {
        if (protectedRegion == null) return;
        
        int closestX = this.closest(location.getBlockX(), protectedRegion.getMinimumPoint().getBlockX(), protectedRegion.getMaximumPoint().getBlockX());
        int closestZ = this.closest(location.getBlockZ(), protectedRegion.getMinimumPoint().getBlockZ(), protectedRegion.getMaximumPoint().getBlockZ());
        
        boolean updateX = Math.abs(location.getX() - closestX) < 8;
        boolean updateZ = Math.abs(location.getZ() - closestZ) < 8;
        
        if(!updateX && !updateZ) return;
     
        ArrayList<Location> list = new ArrayList<Location>();
        if(updateX) {
        	for(int y = -4; y < 5; y++) {
                for(int z = -7; z < 8; z++) {
                    if(this.isInside(protectedRegion.getMinimumPoint().getBlockZ(), protectedRegion.getMaximumPoint().getBlockZ(), location.getBlockZ() + z)) {
                        Location location1 = new Location(location.getWorld(), Double.valueOf(closestX), Double.valueOf(location.getBlockY() + y), Double.valueOf(location.getBlockZ() + z));
                        if(!list.contains(location1) && !location1.getBlock().getType().isOccluding()) {
                            list.add(location1);
                        }
                    }
                }
            }
        } 
        if(updateZ) {
        	for(int y = -4; y < 5; y++) {
                for(int x = -7; x < 8; x++) {
                    if(this.isInside(protectedRegion.getMinimumPoint().getBlockX(), protectedRegion.getMaximumPoint().getBlockX(), location.getBlockX() + x)) {
                        Location location2 = new Location(location.getWorld(), Double.valueOf(location.getBlockX() + x), Double.valueOf(location.getBlockY() + y), Double.valueOf(closestZ));
                        if(!list.contains(location2) && !location2.getBlock().getType().isOccluding()) {
                            list.add(location2);
                        }
                    }
                }
            }
        }
        this.updateWorldGuard(player, list);
	}

    private int closest(int player, int... array) {
	    int current = array[0];
	    for(int i = 0; i < array.length; i++) {
	        if(Math.abs(player - array[i]) < Math.abs((player - current))) {
	            current = array[i];
	        }
	    }
	    return current;
	}

    private boolean isInside(int a, int b, int c) {
		return Math.abs(a - b) == Math.abs(c - a) + Math.abs(c - b);
	}
	
    private void updateWorldGuard(Player player, List<Location> list) {
        if(this.locationsWorldGuard.containsKey(player.getUniqueId())) {
            
        	for(Location location : this.locationsWorldGuard.get(player.getUniqueId())) {
               
        		if(!list.contains(location)) {
                	Block block = location.getBlock();
                   
                	player.sendBlockChange(location, block.getTypeId(), block.getData());
                }    
            }
        	
            for(Location location2 : list) {
                player.sendBlockChange(location2, 95, (byte)14);
            }
            
        } else {
            for (final Location location3 : list) {
                player.sendBlockChange(location3, 95, (byte)14);
            }
        }
        
        this.locationsWorldGuard.put(player.getUniqueId(), list);
    }
	
    private void removeGlass(Player player) {
        if(this.locationsWorldGuard.containsKey(player.getUniqueId())) {
            for(Location location : this.locationsWorldGuard.get(player.getUniqueId())) {
                Block block = location.getBlock();
                player.sendBlockChange(location, block.getTypeId(), block.getData());
            }
            this.locationsWorldGuard.remove(player.getUniqueId());
        }
    }
}
