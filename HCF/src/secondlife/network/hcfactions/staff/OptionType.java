package secondlife.network.hcfactions.staff;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.UUID;

@Getter
public enum OptionType {

	DAMAGE("Damage", new ArrayList<UUID>()),
	PLACE("Place", new ArrayList<UUID>()),
	BREAK("Break", new ArrayList<UUID>()),
	PICKUP("Pickup", new ArrayList<UUID>()),
	INTERACT("Interact", new ArrayList<UUID>()),
	CHEST("Chest", new ArrayList<UUID>());
	
	@Setter private String name;
	@Setter private ArrayList<UUID> players;
	
	OptionType(String name, ArrayList<UUID> players) {
		this.name = name;
		this.players = players;
	}
}
