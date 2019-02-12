package secondlife.network.uhc.scenario.type;

import org.bukkit.Material;
import org.bukkit.event.Listener;

import secondlife.network.uhc.scenario.Scenario;

public class BackPacksScenario extends Scenario implements Listener {

	public BackPacksScenario() {
		super("BackPacks", Material.CHEST, "Use - /backpack to open the party inventory.");
	}

}
