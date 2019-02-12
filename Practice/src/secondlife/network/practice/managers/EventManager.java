package secondlife.network.practice.managers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.events.EventState;
import secondlife.network.practice.events.PracticeEvent;
import secondlife.network.practice.events.oitc.OITCEvent;
import secondlife.network.practice.events.parkour.ParkourEvent;
import secondlife.network.practice.events.sumo.SumoEvent;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.CustomLocation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class EventManager {
	private final Map<Class<? extends PracticeEvent>, PracticeEvent> events = new HashMap<>();

	private final Practice plugin = Practice.getInstance();

	private HashMap<UUID, PracticeEvent> spectators;

	@Setter private long cooldown;
	private final World eventWorld;

	public EventManager() {
		Arrays.asList(
				SumoEvent.class,
				OITCEvent.class,
				ParkourEvent.class
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

	public PracticeEvent getByName(String name) {
		return events.values().stream().filter(event -> event.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())).findFirst().orElse(null);
	}

	public void hostEvent(PracticeEvent event, Player host) {

		event.setState(EventState.WAITING);
		event.setHost(host);
		event.startCountdown();
	}

	private void addEvent(Class<? extends PracticeEvent> clazz) {
		PracticeEvent event = null;

		try {
			event = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		events.put(clazz, event);
	}

	public void addSpectatorSumo(Player player, PracticeData playerData, SumoEvent event) {
		this.addSpectator(player, playerData, event);

		if (event.getSpawnLocations().size() == 1) {
			player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
		} else {
			List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
			player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
		}


		for(Player eventPlayer : event.getBukkitPlayers()) {
			player.showPlayer(eventPlayer);
		}

		player.setGameMode(GameMode.SPECTATOR);

		player.setAllowFlight(true);
		player.setFlying(true);
	}

	public void addSpectatorOITC(Player player, PracticeData playerData, OITCEvent event) {
		this.addSpectator(player, playerData, event);

		if (event.getSpawnLocations().size() == 1) {
			player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
		} else {
			List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
			player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
		}


		for(Player eventPlayer : event.getBukkitPlayers()) {
			player.showPlayer(eventPlayer);
		}

		player.setGameMode(GameMode.SPECTATOR);

		player.setAllowFlight(true);
		player.setFlying(true);
	}

	public void addSpectatorParkour(Player player, PracticeData playerData, ParkourEvent event) {

		this.addSpectator(player, playerData, event);

		if (event.getSpawnLocations().size() == 1) {
			player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
		} else {
			List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
			player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
		}


		for(Player eventPlayer : event.getBukkitPlayers()) {
			player.showPlayer(eventPlayer);
		}

		player.setGameMode(GameMode.SPECTATOR);

		player.setAllowFlight(true);
		player.setFlying(true);
	}

	private void addSpectator(Player player, PracticeData playerData, PracticeEvent event) {

		playerData.setPlayerState(PlayerState.SPECTATING);
		this.spectators.put(player.getUniqueId(), event);

		player.getInventory().setContents(this.plugin.getItemManager().getSpecItems());
		player.updateInventory();

		Bukkit.getOnlinePlayers().forEach(online -> {
			online.hidePlayer(player);
			player.hidePlayer(online);
		});

	}

	public void removeSpectator(Player player) {
		this.getSpectators().remove(player.getUniqueId());
		PracticeData.sendToSpawnAndReset(player);
	}


	public boolean isPlaying(Player player, PracticeEvent event) {
		return event.getPlayers().containsKey(player.getUniqueId());
	}

	public PracticeEvent getEventPlaying(Player player) {
		return this.events.values().stream().filter(event -> this.isPlaying(player, event)).findFirst().orElse(null);
	}
}