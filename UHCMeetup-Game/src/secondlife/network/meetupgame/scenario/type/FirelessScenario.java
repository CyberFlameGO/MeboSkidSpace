package secondlife.network.meetupgame.scenario.type;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import secondlife.network.meetupgame.scenario.Scenario;

public class FirelessScenario extends Scenario implements Listener {
	
	public FirelessScenario() {
		super("Fireless", Material.LAVA_BUCKET, "You can't take fire damage.");
	}

	public static void handleEntityDamage(Entity entity, DamageCause cause, EntityDamageEvent event) {
		if(!(entity instanceof Player)) return;

		if(cause == DamageCause.FIRE || cause == DamageCause.FIRE_TICK || cause == DamageCause.LAVA) {
			event.setCancelled(true);
		}
	}
}
