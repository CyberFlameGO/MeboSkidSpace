package secondlife.network.uhc.scenario;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.managers.ScenarioManager;
import secondlife.network.vituz.utilties.Msg;

@Getter
@Setter
public abstract class Scenario {

	protected UHC plugin = UHC.getInstance();

	private String name;
	private String[] features;
	private Material material;
	private boolean enabled;

	public Scenario(String name, Material icon, String... features) {
		this.name = name;
		this.features = features;
		this.material = icon;
		this.enabled = false;
		
		ScenarioManager.getScenarios().add(this);
	}

	public void toggle() {
		if(!this.enabled) {
			//Bukkit.getPluginManager().registerEvents((Listener) this, UHC.getInstance());
			
			Msg.sendMessage("&eScenario &d" + name + " &ehas been &aEnabled&e.");
			
			this.enabled = true;
		} else {
			//HandlerList.unregisterAll((Listener) this);
			
			Msg.sendMessage("&eScenario &d" + name + " &ehas been &cDisabled&e.");
			
			this.enabled = false;
		}
	}

	public void disableScenarios() {
		if(enabled) {
			//HandlerList.unregisterAll((Listener) this);

			Msg.sendMessage("&eAll scenarios have been disabled.");

			this.enabled = false;
		}
	}
}
