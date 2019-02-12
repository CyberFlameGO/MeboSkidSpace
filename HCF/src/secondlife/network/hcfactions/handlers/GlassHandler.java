
package secondlife.network.hcfactions.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.claim.ClaimZone;
import secondlife.network.hcfactions.factions.type.ClaimableFaction;
import secondlife.network.hcfactions.timers.SpawnTagHandler;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.cuboid.Cuboid;
import secondlife.network.vituz.visualise.VisualType;
import secondlife.network.vituz.visualise.VisualiseHandler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class GlassHandler extends Handler implements Listener {

    public static int border_height_below_diff = 3;
    public static int border_height_above_diff = 4;
    public static int border_horizontal_distance = 5;

    public GlassHandler(HCF plugin) {
    	super(plugin);

    	setup();

        Bukkit.getPluginManager().registerEvents(this, this.getInstance());
    }

    public void setup() {
        new BukkitRunnable() {
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Location location = player.getLocation();

                    handlePositionChanged(player, player.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
                }
            }
        }.runTaskTimerAsynchronously(this.getInstance(), 2L, 2L);
    }

    /*@EventHandler
    public void onPlayerMove(PlayerMoveByBlockEvent event) {
        if(event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;

        handlePositionChanged(event.getPlayer(), event.getTo().getWorld(), event.getTo().getBlockX(), event.getTo().getBlockY(), event.getTo().getBlockZ());
    }*/

    private static void handlePositionChanged(Player player, World toWorld, int toX, int toY, int toZ) {
        VisualType visualType = VisualType.RED;

		VisualiseHandler.clearVisualBlocks(player, visualType, visualBlock -> {
            Location other = visualBlock.getLocation();

            return other.getWorld().equals(toWorld) && (Math.abs(toX - other.getBlockX()) > border_horizontal_distance || Math.abs(toY - other.getBlockY()) > border_height_above_diff || Math.abs(toZ - other.getBlockZ()) > border_horizontal_distance);
        });

		if(!SpawnTagHandler.isActive(player)) return;

        int minHeight = toY - border_height_below_diff;
        int maxHeight = toY + border_height_above_diff;
        
        int minX = toX - border_horizontal_distance;
        int maxX = toX + border_horizontal_distance;
        int minZ = toZ - border_horizontal_distance;
        int maxZ = toZ + border_horizontal_distance;

        Collection<ClaimZone> added = new HashSet<ClaimZone>();
        for(int x = minX; x < maxX; ++x) {
            for(int z = minZ; z < maxZ; ++z) {
                Faction faction = RegisterHandler.getInstancee().getFactionManager().getFactionAt(toWorld, x, z);

                if(faction instanceof ClaimableFaction) {
                    if(!faction.isSafezone()) continue;

                    Collection<ClaimZone> claims = ((ClaimableFaction)faction).getClaims();
                    for(ClaimZone claim : claims) {
                        if(toWorld.equals(claim.getWorld())) {
                            added.add(claim);
                        }
                    }
                }
            }
        }

        if(!added.isEmpty()) {
            Iterator<ClaimZone> iterator = added.iterator();
            while(iterator.hasNext()) {
                ClaimZone claim = iterator.next();
                Collection<Vector> edges = claim.edges(); 
                
                for(Vector edge : edges) {
                    if(Math.abs(edge.getBlockX() - toX) > border_horizontal_distance) continue;
                    if(Math.abs(edge.getBlockZ() - toZ) > border_horizontal_distance) continue;

                    Location location = edge.toLocation(toWorld);
                    
                    if(location != null) {
                        Location first = location.clone();
                        
                        first.setY(minHeight);

                        Location second = location.clone();
                        
                        second.setY(maxHeight);

                        VisualiseHandler.generate(player, new Cuboid(first, second), visualType, false);
                    }
                }

                iterator.remove();
            }
        }
    }
}
