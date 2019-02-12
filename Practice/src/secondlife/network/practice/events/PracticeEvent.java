package secondlife.network.practice.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.events.oitc.OITCEvent;
import secondlife.network.practice.events.oitc.OITCPlayer;
import secondlife.network.practice.events.parkour.ParkourEvent;
import secondlife.network.practice.events.sumo.SumoEvent;
import secondlife.network.practice.events.sumo.SumoPlayer;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.CustomLocation;
import secondlife.network.practice.utilties.PlayerUtil;
import secondlife.network.practice.utilties.event.EventStartEvent;
import secondlife.network.vituz.providers.nametags.VituzNametagHandler;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class PracticeEvent<K extends EventPlayer> {
	private final Practice plugin = Practice.getInstance();

	private final String name;

	private int limit = 30;
	private Player host;
	private EventState state = EventState.UNANNOUNCED;

	public void startCountdown() {
		// Restart Logic
		if (getCountdownTask().isEnded()) {
			getCountdownTask().setTimeUntilStart(getCountdownTask().getCountdownTime());
			getCountdownTask().setEnded(false);
		} else {
			getCountdownTask().runTaskTimerAsynchronously(plugin, 20L, 20L);
		}
	}

	public void sendMessage(String message) {
		getBukkitPlayers().forEach(player -> player.sendMessage(message));
	}

	public Set<Player> getBukkitPlayers() {
		return getPlayers().keySet().stream()
				.filter(uuid -> plugin.getServer().getPlayer(uuid) != null)
				.map(plugin.getServer()::getPlayer)
				.collect(Collectors.toSet());
	}

	public void join(Player player) {
		if(this.getPlayers().size() >= this.limit) {
			return;
		}

		this.plugin.getQueueManager().removePlayerFromQueue(player);

		PracticeData playerData = PracticeData.getByName(player.getName());
		playerData.setPlayerState(PlayerState.EVENT);

		PlayerUtil.clearPlayer(player);

		if (onJoin() != null) {
			onJoin().accept(player);
		}

		if (getSpawnLocations().size() == 1) {
			player.teleport(getSpawnLocations().get(0).toBukkitLocation());
		} else {
			List<CustomLocation> spawnLocations = new ArrayList<>(getSpawnLocations());
			player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
		}

		PracticeData.giveLobbyItems(player);

		for(Player other : this.getBukkitPlayers()) {
			other.showPlayer(player);
			player.showPlayer(other);
		}

		VituzNametagHandler.reloadPlayer(player);
		VituzNametagHandler.reloadOthersFor(player);

		this.sendMessage(ChatColor.YELLOW + player.getName() + " has joined the event. (" + this.getPlayers().size() + " player" + (this.getPlayers().size() == 1 ? "": "s") + ")");
	}

	public void leave(Player player) {

		if(this instanceof OITCEvent) {
			OITCEvent oitcEvent = (OITCEvent) this;
			OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);
			oitcPlayer.setState(OITCPlayer.OITCState.ELIMINATED);
		}

		if (onDeath() != null) {
			onDeath().accept(player);
		}

		getPlayers().remove(player.getUniqueId());

		PracticeData.sendToSpawnAndReset(player);
	}

	public void start() {
		new EventStartEvent(this).call();

		setState(EventState.STARTED);

		onStart();

		this.plugin.getEventManager().setCooldown(0L);
	}

	public void end() {

		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> plugin.getEventManager().getEventWorld().getPlayers().forEach(player -> PracticeData.sendToSpawnAndReset(player)), 2L);
		
		this.plugin.getEventManager().setCooldown(System.currentTimeMillis() + (60 * 5) * 1000L);

		if(this instanceof SumoEvent) {

			SumoEvent sumoEvent = (SumoEvent) this;
			for(SumoPlayer sumoPlayer : sumoEvent.getPlayers().values()) {

				if(sumoPlayer.getFightTask() != null) {
					sumoPlayer.getFightTask().cancel();
				}
			}

			if(sumoEvent.getWaterCheckTask() != null) {
				sumoEvent.getWaterCheckTask().cancel();
			}
		}

		else if(this instanceof OITCEvent) {
			OITCEvent oitcEvent = (OITCEvent) this;

			if(oitcEvent.getGameTask() != null) {
				oitcEvent.getGameTask().cancel();
			}
		}

		else if(this instanceof ParkourEvent) {
			ParkourEvent parkourEvent = (ParkourEvent) this;

			if(parkourEvent.getGameTask() != null) {
				parkourEvent.getGameTask().cancel();
			}

			if(parkourEvent.getWaterCheckTask() != null) {
				parkourEvent.getWaterCheckTask().cancel();
			}
		}

		getPlayers().clear();

		setState(EventState.UNANNOUNCED);

		Iterator<UUID> iterator = this.plugin.getEventManager().getSpectators().keySet().iterator();

		while(iterator.hasNext()) {
			UUID spectatorUUID = iterator.next();
			Player spectator = Bukkit.getPlayer(spectatorUUID);

			if(spectator != null) {
				this.plugin.getServer().getScheduler().runTask(this.plugin, () -> PracticeData.sendToSpawnAndReset(spectator));
				iterator.remove();
			}
		}

		this.plugin.getEventManager().getSpectators().clear();

		getCountdownTask().setEnded(true);
	}

	public K getPlayer(Player player) {
		return getPlayer(player.getUniqueId());
	}

	public K getPlayer(UUID uuid) {
		return getPlayers().get(uuid);
	}

	public abstract Map<UUID, K> getPlayers();

	public abstract EventCountdownTask getCountdownTask();

	public abstract List<CustomLocation> getSpawnLocations();

	public abstract void onStart();

	public abstract Consumer<Player> onJoin();

	public abstract Consumer<Player> onDeath();
}
