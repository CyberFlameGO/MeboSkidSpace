package secondlife.network.meetupgame.scenario;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import secondlife.network.meetupgame.MeetupGame;

/**
 * Created by Marko on 11.06.2018.
 */

@Getter
@Setter
public abstract class Scenario {

	private String name;
	private String[] features;
	private Material material;
	private boolean enabled;
	private int votes;

	public Scenario(String name, Material icon, String... features) {
		this.name = name;
		this.features = features;
		this.material = icon;
		this.enabled = false;
		this.votes = 0;
		
		ScenarioManager.getScenarios().add(this);
	}

	public void toggle() {
		if(!this.enabled) {
			Bukkit.getPluginManager().registerEvents((Listener) this, MeetupGame.getInstance());

			this.enabled = true;
		} else {
			HandlerList.unregisterAll((Listener) this);

			this.enabled = false;
		}
	}
}
