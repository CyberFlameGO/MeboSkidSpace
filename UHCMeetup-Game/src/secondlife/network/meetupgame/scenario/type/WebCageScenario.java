package secondlife.network.meetupgame.scenario.type;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.meetupgame.utilties.MeetupUtils;

import java.util.List;

public class WebCageScenario extends Scenario implements Listener {

	public WebCageScenario() {
		super("WebCage", Material.WEB, "When you kill a player a sphere of", "cobwebs surrounds you.");
	}

	public static void handleDeath(Entity entity) {
		Player player = (Player) entity;

		Location location = player.getLocation();
		Player killer = player.getKiller();

		if(killer == null) {
			return;
		}

		List<Location> locations = MeetupUtils.getSphere(location, 5, true);

		for(Location blocks : locations) {
			if(blocks.getBlock().getType() == Material.AIR) {
				blocks.getBlock().setType(Material.WEB);
			}
		}
	}
}