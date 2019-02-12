
package secondlife.network.meetupgame.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.utilties.Manager;
import secondlife.network.vituz.utilties.cuboid.Cuboid;
import secondlife.network.vituz.visualise.VisualType;
import secondlife.network.vituz.visualise.VisualiseHandler;

import java.util.Arrays;
import java.util.Iterator;

public class GlassManager extends Manager {

    private int border_height_below_diff = 3;
    private int border_height_above_diff = 4;
    private int border_horizontal_distance = 5;

    public GlassManager(MeetupGame plugin) {
		super(plugin);

		handleSetup();
    }

    private void handleSetup() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> Bukkit.getOnlinePlayers().forEach(player -> {
            if(Arrays.asList(150, 100, 50, 25, 10).contains(GameManager.getGameData().getBorder())) {
                handleMove(player, player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
            }
        }), 2L, 2L);
    }

    private void handleMove(Player player, World toWorld, int toX, int toY, int toZ) {
        VisualType visualType = VisualType.RED;

        VisualiseHandler.clearVisualBlocks(player, visualType, visualBlock -> {
            Location other = visualBlock != null ? visualBlock.getLocation() : null;

            return other.getWorld().equals(toWorld)
                    && (Math.abs(toX - other.getX()) > border_horizontal_distance
                    || Math.abs(toY - other.getY()) > border_height_above_diff
                    || Math.abs(toZ - other.getZ()) > border_horizontal_distance);
        });

        int minHeight = toY - border_height_below_diff;
        int maxHeight = toY + border_height_above_diff;
        
        Location loc = new Location(toWorld, GameManager.getGameData().getBorder(), 0, -GameManager.getGameData().getBorder());
        Iterator<Vector> iterator = new Cuboid(loc, new Location(toWorld, -GameManager.getGameData().getBorder(), 0, GameManager.getGameData().getBorder())).edges().iterator();
        
        while(iterator.hasNext()) {
			Vector vector = iterator.next();
			
			if(Math.abs(vector.getBlockX() - toX) > border_horizontal_distance) continue;
            if(Math.abs(vector.getBlockZ() - toZ) > border_horizontal_distance) continue;
			
			Location location = vector.toLocation(toWorld);
			
			if(location != null) {
				Location first = location.clone();

				first.setY(minHeight);

				Location second = location.clone();

				second.setY(maxHeight);
                
				VisualiseHandler.generate(player, new Cuboid(first, second), visualType, false).size();
			}
			
			iterator.remove();
        }
	}
}
