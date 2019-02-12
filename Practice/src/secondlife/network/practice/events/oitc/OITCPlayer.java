package secondlife.network.practice.events.oitc;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitTask;
import secondlife.network.practice.events.EventPlayer;
import secondlife.network.practice.events.PracticeEvent;

import java.util.UUID;

@Setter
@Getter
public class OITCPlayer extends EventPlayer {

	private OITCState state = OITCState.WAITING;
	private int score = 0;
	private int lives = 5;
	private BukkitTask respawnTask;
	private OITCPlayer lastKiller;

	public OITCPlayer(UUID uuid, PracticeEvent event) {
		super(uuid, event);
	}

	public enum OITCState {
		WAITING, PREPARING, FIGHTING, RESPAWNING, ELIMINATED
	}
}
