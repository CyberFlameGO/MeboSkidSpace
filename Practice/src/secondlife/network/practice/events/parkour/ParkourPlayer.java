package secondlife.network.practice.events.parkour;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.practice.events.EventPlayer;
import secondlife.network.practice.events.PracticeEvent;
import secondlife.network.practice.utilties.CustomLocation;

import java.util.UUID;

@Setter
@Getter
public class ParkourPlayer extends EventPlayer {

	private ParkourState state = ParkourState.WAITING;
	private CustomLocation lastCheckpoint;
	private int checkpointId;

	public ParkourPlayer(UUID uuid, PracticeEvent event) {
		super(uuid, event);
	}

	public enum ParkourState {
		WAITING, INGAME
	}
}
