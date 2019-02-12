package secondlife.network.hcfactions.utilties.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import secondlife.network.hcfactions.events.KitMapEvent;

@Getter
@RequiredArgsConstructor
public class EventStartEvent extends BaseEvent {
	private final KitMapEvent event;
}
