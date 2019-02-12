package secondlife.network.practice.utilties.event;

import secondlife.network.practice.events.PracticeEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventStartEvent extends BaseEvent {
	private final PracticeEvent event;
}
