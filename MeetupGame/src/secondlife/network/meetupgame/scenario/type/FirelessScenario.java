package secondlife.network.meetupgame.scenario.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import secondlife.network.meetupgame.scenario.Scenario;

/**
 * Created by Marko on 11.06.2018.
 */
public class FirelessScenario extends Scenario implements Listener {
	
	public FirelessScenario() {
		super("Fireless", Material.FIRE, "You can't take fire damage!");
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player)) return;

		if(event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.LAVA) {
			event.setCancelled(true);
		}
	}
}
