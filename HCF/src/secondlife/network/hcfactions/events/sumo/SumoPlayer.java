package secondlife.network.hcfactions.events.sumo;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitTask;
import secondlife.network.hcfactions.events.EventPlayer;
import secondlife.network.hcfactions.events.KitMapEvent;

import java.util.UUID;

@Setter
@Getter
public class SumoPlayer extends EventPlayer {

	private SumoState state = SumoState.WAITING;
	private BukkitTask fightTask;
	private SumoPlayer fighting;

	public SumoPlayer(UUID uuid, KitMapEvent event) {
		super(uuid, event);
	}

	public enum SumoState {
		WAITING, PREPARING, FIGHTING, ELIMINATED
	}
}
