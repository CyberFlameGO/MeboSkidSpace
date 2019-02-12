package secondlife.network.hcfactions.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class EventPlayer {
	private final UUID uuid;
	private final KitMapEvent event;
}
