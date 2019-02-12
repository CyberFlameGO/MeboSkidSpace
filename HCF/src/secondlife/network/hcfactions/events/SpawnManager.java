package secondlife.network.hcfactions.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import secondlife.network.hcfactions.utilties.CustomLocation;
import secondlife.network.hcfactions.utilties.file.UtilitiesFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SpawnManager {

	private CustomLocation sumoLocation;
	private CustomLocation sumoFirst;
	private CustomLocation sumoSecond;
	private CustomLocation sumoMin;
	private CustomLocation sumoMax;

	public SpawnManager() {
		this.loadConfig();
	}

	private void loadConfig() {
		FileConfiguration config = UtilitiesFile.configuration;

		if(config.contains("sumoLocation")) {
			this.sumoLocation = CustomLocation.stringToLocation(config.getString("sumoLocation"));
			this.sumoMin = CustomLocation.stringToLocation(config.getString("sumoMin"));
			this.sumoMax = CustomLocation.stringToLocation(config.getString("sumoMax"));
			this.sumoFirst = CustomLocation.stringToLocation(config.getString("sumoFirst"));
			this.sumoSecond = CustomLocation.stringToLocation(config.getString("sumoSecond"));
		}
	}

	public void saveConfig() {
		FileConfiguration config = UtilitiesFile.configuration;

		config.set("sumoLocation", CustomLocation.locationToString(this.sumoLocation));
		config.set("sumoMin", CustomLocation.locationToString(this.sumoMin));
		config.set("sumoMax", CustomLocation.locationToString(this.sumoMax));
		config.set("sumoFirst", CustomLocation.locationToString(this.sumoFirst));
		config.set("sumoSecond", CustomLocation.locationToString(this.sumoSecond));

		UtilitiesFile.save();
	}

	private List<String> fromLocations(List<CustomLocation> locations) {

		List<String> toReturn = new ArrayList<>();
		for(CustomLocation location : locations) {
			toReturn.add(CustomLocation.locationToString(location));
		}

		return toReturn;
	}
}

