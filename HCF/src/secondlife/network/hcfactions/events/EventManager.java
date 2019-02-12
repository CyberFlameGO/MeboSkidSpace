package secondlife.network.hcfactions.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.events.sumo.SumoEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class EventManager {
	private final Map<Class<? extends KitMapEvent>, KitMapEvent> events = new HashMap<>();

	private final HCF plugin = HCF.getInstance();

	private HashMap<UUID, KitMapEvent> spectators;

	@Setter private long cooldown;
	private final World eventWorld;

	public EventManager() {
		Arrays.asList(
				SumoEvent.class
		).forEach(clazz -> this.addEvent(clazz));

		boolean newWorld;

		if(Bukkit.getWorld("event") == null) {
			eventWorld = Bukkit.createWorld(new WorldCreator("event"));
			newWorld = true;
		} else {
			eventWorld = Bukkit.getWorld("event");
			newWorld = false;
		}

		this.spectators = new HashMap<>();

		this.cooldown = 0L;

		if (eventWorld != null) {

			if(newWorld) {
				Bukkit.getWorlds().add(eventWorld);
			}

			eventWorld.setTime(2000L);
			eventWorld.setGameRuleValue("doDaylightCycle", "false");
			eventWorld.setGameRuleValue("doMobSpawning", "false");
			eventWorld.setStorm(false);
			eventWorld.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
		}
	}

	public KitMapEvent getByName(String name) {
		return events.values().stream().filter(event -> event.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())).findFirst().orElse(null);
	}

	public void hostEvent(KitMapEvent event, Player host) {

		event.setState(EventState.WAITING);
		event.setHost(host);
		event.startCountdown();
	}

	private void addEvent(Class<? extends KitMapEvent> clazz) {
		KitMapEvent event = null;

		try {
			event = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		events.put(clazz, event);
	}

	public boolean isPlaying(Player player, KitMapEvent event) {
		return event.getPlayers().containsKey(player.getUniqueId());
	}

	public KitMapEvent getEventPlaying(Player player) {
		return this.events.values().stream().filter(event -> this.isPlaying(player, event)).findFirst().orElse(null);
	}
}