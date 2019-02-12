package secondlife.network.vituz.punishments;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class PunishmentQueue {
	
	@Getter
	private static Set<PunishmentQueue> queues = new HashSet<>();

	private String name;
	private PunishmentType type;

	public PunishmentQueue(String name, PunishmentType type) {
		this.name = name;
		this.type = type;
		
		queues.add(this);
	}

	public static PunishmentQueue get(String name, PunishmentType type) {
		for(PunishmentQueue queue : PunishmentQueue.queues) {
			if(queue.getType() == type && queue.getName().equals(name)) {
				return queue;
			}
		}
		
		return null;
	}
}
