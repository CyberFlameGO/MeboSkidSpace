package secondlife.network.practice.managers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import secondlife.network.practice.Practice;
import secondlife.network.practice.utilties.CustomLocation;
import secondlife.network.practice.utilties.file.ConfigFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SpawnManager {
	private Practice plugin = Practice.getInstance();

	private CustomLocation spawnLocation;
	private CustomLocation spawnMin;
	private CustomLocation spawnMax;

	private CustomLocation editorLocation;
	private CustomLocation editorMin;
	private CustomLocation editorMax;

	private CustomLocation sumoLocation;
	private CustomLocation sumoFirst;
	private CustomLocation sumoSecond;
	private CustomLocation sumoMin;
	private CustomLocation sumoMax;

	private CustomLocation oitcLocation;
	private List<CustomLocation> oitcSpawnpoints;
	private CustomLocation oitcMin;
	private CustomLocation oitcMax;

	private CustomLocation parkourLocation;
	private CustomLocation parkourGameLocation;
	private CustomLocation parkourMin;
	private CustomLocation parkourMax;

	private CustomLocation redroverLocation;
	private CustomLocation redroverFirst;
	private CustomLocation redroverSecond;
	private CustomLocation redroverMin;
	private CustomLocation redroverMax;

	public SpawnManager() {
		this.oitcSpawnpoints = new ArrayList<>();
		this.loadConfig();
	}

	private void loadConfig() {
		FileConfiguration config = ConfigFile.configuration;
		if (config.contains("spawnLocation")) {
			this.spawnLocation = CustomLocation.stringToLocation(config.getString("spawnLocation"));
			this.spawnMin = CustomLocation.stringToLocation(config.getString("spawnMin"));
			this.spawnMax = CustomLocation.stringToLocation(config.getString("spawnMax"));
		}

		if(config.contains("editorLocation")) {
			this.editorLocation = CustomLocation.stringToLocation(config.getString("editorLocation"));
			this.editorMin = CustomLocation.stringToLocation(config.getString("editorMin"));
			this.editorMax = CustomLocation.stringToLocation(config.getString("editorMax"));
		}

		if(config.contains("sumoLocation")) {
			this.sumoLocation = CustomLocation.stringToLocation(config.getString("sumoLocation"));
			this.sumoMin = CustomLocation.stringToLocation(config.getString("sumoMin"));
			this.sumoMax = CustomLocation.stringToLocation(config.getString("sumoMax"));
			this.sumoFirst = CustomLocation.stringToLocation(config.getString("sumoFirst"));
			this.sumoSecond = CustomLocation.stringToLocation(config.getString("sumoSecond"));
		}

		if(config.contains("oitcLocation")) {
			this.oitcLocation = CustomLocation.stringToLocation(config.getString("oitcLocation"));
			this.oitcMin = CustomLocation.stringToLocation(config.getString("oitcMin"));
			this.oitcMax = CustomLocation.stringToLocation(config.getString("oitcMax"));

			for(String spawnpoint : config.getStringList("oitcSpawnpoints")) {
				this.oitcSpawnpoints.add(CustomLocation.stringToLocation(spawnpoint));
			}
		}

		if(config.contains("redroverLocation")) {
			this.redroverLocation = CustomLocation.stringToLocation(config.getString("redroverLocation"));
			this.redroverMin = CustomLocation.stringToLocation(config.getString("redroverMin"));
			this.redroverMax = CustomLocation.stringToLocation(config.getString("redroverMax"));
			this.redroverFirst = CustomLocation.stringToLocation(config.getString("redroverFirst"));
			this.redroverSecond = CustomLocation.stringToLocation(config.getString("redroverSecond"));
		}

		if(config.contains("parkourLocation")) {
			this.parkourLocation = CustomLocation.stringToLocation(config.getString("parkourLocation"));
			this.parkourGameLocation = CustomLocation.stringToLocation(config.getString("parkourGameLocation"));
			this.parkourMin = CustomLocation.stringToLocation(config.getString("parkourMin"));
			this.parkourMax = CustomLocation.stringToLocation(config.getString("parkourMax"));
		}
	}

	public void saveConfig() {
		FileConfiguration config = ConfigFile.configuration;
		config.set("spawnLocation", CustomLocation.locationToString(this.spawnLocation));
		config.set("spawnMin", CustomLocation.locationToString(this.spawnMin));
		config.set("spawnMax", CustomLocation.locationToString(this.spawnMax));

		config.set("editorLocation", CustomLocation.locationToString(this.editorLocation));
		config.set("editorMin", CustomLocation.locationToString(this.editorMin));
		config.set("editorMax", CustomLocation.locationToString(this.editorMax));

		config.set("sumoLocation", CustomLocation.locationToString(this.sumoLocation));
		config.set("sumoMin", CustomLocation.locationToString(this.sumoMin));
		config.set("sumoMax", CustomLocation.locationToString(this.sumoMax));
		config.set("sumoFirst", CustomLocation.locationToString(this.sumoFirst));
		config.set("sumoSecond", CustomLocation.locationToString(this.sumoSecond));

		config.set("oitcLocation", CustomLocation.locationToString(this.oitcLocation));
		config.set("oitcMin", CustomLocation.locationToString(this.oitcMin));
		config.set("oitcMax", CustomLocation.locationToString(this.oitcMax));
		config.set("oitcSpawnpoints", this.fromLocations(this.oitcSpawnpoints));

		config.set("parkourLocation", CustomLocation.locationToString(this.parkourLocation));
		config.set("parkourGameLocation", CustomLocation.locationToString(this.parkourGameLocation));
		config.set("parkourMin", CustomLocation.locationToString(this.parkourMin));
		config.set("parkourMax", CustomLocation.locationToString(this.parkourMax));

		config.set("redroverLocation", CustomLocation.locationToString(this.redroverLocation));
		config.set("redroverMin", CustomLocation.locationToString(this.redroverMin));
		config.set("redroverMax", CustomLocation.locationToString(this.redroverMax));
		config.set("redroverFirst", CustomLocation.locationToString(this.redroverFirst));
		config.set("redroverSecond", CustomLocation.locationToString(this.redroverSecond));

		ConfigFile.save();
	}

	private List<String> fromLocations(List<CustomLocation> locations) {

		List<String> toReturn = new ArrayList<>();
		for(CustomLocation location : locations) {
			toReturn.add(CustomLocation.locationToString(location));
		}

		return toReturn;
	}
}

