package secondlife.network.paik.utilties.events;

import java.util.Map;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class ModListRetrieveEvent extends PlayerEvent {

	private Map<String, String> mods;

	public ModListRetrieveEvent(Player player, Map<String, String> mods) {
		super(player);

		this.mods = mods;
	}

}
