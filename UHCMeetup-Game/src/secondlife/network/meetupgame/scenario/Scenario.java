package secondlife.network.meetupgame.scenario;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.managers.ScenarioManager;

@Getter
@Setter
public abstract class Scenario {

	protected static MeetupGame plugin = MeetupGame.getInstance();

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
}
