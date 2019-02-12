package secondlife.network.meetupgame.scenario.type;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import secondlife.network.meetupgame.scenario.Scenario;

/**
 * Created by Marko on 11.06.2018.
 */
public class DefaultScenario extends Scenario implements Listener {

    public DefaultScenario() {
        super("Default", Material.INK_SACK, "Basic game, no scenarios!");
    }
}
