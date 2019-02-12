
package secondlife.network.meetupgame.scenario.type;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import secondlife.network.meetupgame.scenario.Scenario;

public class NoFallDamageScenario extends Scenario implements Listener {

	public NoFallDamageScenario() {
		super("NoFallDamage", Material.DIAMOND_BOOTS, "You can't take fall damage.");
	}

	public static void handleEntityDamage(Entity entity, EntityDamageEvent.DamageCause cause, EntityDamageEvent event) {
		if(!(entity instanceof Player)) {
			return;
		}

		if(cause == EntityDamageEvent.DamageCause.FALL) {
			event.setCancelled(true);
		}
	}
}
