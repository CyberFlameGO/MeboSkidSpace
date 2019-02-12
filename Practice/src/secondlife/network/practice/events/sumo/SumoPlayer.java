package secondlife.network.practice.events.sumo;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitTask;
import secondlife.network.practice.events.EventPlayer;
import secondlife.network.practice.events.PracticeEvent;

import java.util.UUID;

@Setter
@Getter
public class SumoPlayer extends EventPlayer {

	private SumoState state = SumoState.WAITING;
	private BukkitTask fightTask;
	private SumoPlayer fighting;

	public SumoPlayer(UUID uuid, PracticeEvent event) {
		super(uuid, event);
	}

	public enum SumoState {
		WAITING, PREPARING, FIGHTING, ELIMINATED
	}
}
