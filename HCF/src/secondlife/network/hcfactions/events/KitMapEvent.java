package secondlife.network.hcfactions.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.events.sumo.SumoEvent;
import secondlife.network.hcfactions.events.sumo.SumoPlayer;
import secondlife.network.hcfactions.staff.handlers.StaffModeHandler;
import secondlife.network.hcfactions.staff.handlers.VanishHandler;
import secondlife.network.hcfactions.utilties.CustomLocation;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.utilties.events.EventStartEvent;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class KitMapEvent<K extends EventPlayer> {
	private final HCF plugin = HCF.getInstance();

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

		if(StaffModeHandler.isInStaffMode(player)) StaffModeHandler.disableStaffMode(player);
		if(VanishHandler.isVanished(player)) VanishHandler.unvanishPlayer(player);

		HCFData playerData = HCFData.getByName(player.getName());
		playerData.setEvent(true);

		HCFUtils.clearPlayer(player);

		if (onJoin() != null) {
			onJoin().accept(player);
		}

		if (getSpawnLocations().size() == 1) {
			player.teleport(getSpawnLocations().get(0).toBukkitLocation());
		} else {
			List<CustomLocation> spawnLocations = new ArrayList<>(getSpawnLocations());
			player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
		}

		HCFData.giveLobbyItems(player);

		for(Player other : this.getBukkitPlayers()) {
			other.showPlayer(player);
			player.showPlayer(other);
		}

		this.sendMessage(ChatColor.YELLOW + player.getName() + " has joined the event. (" + this.getPlayers().size() + " player" + (this.getPlayers().size() == 1 ? "": "s") + ")");
	}

	public void leave(Player player) {
		if (onDeath() != null) {
			onDeath().accept(player);
		}

		getPlayers().remove(player.getUniqueId());

		HCFData.sendToSpawnAndReset(player);
	}

	public void start() {
		new EventStartEvent(this).call();

		setState(EventState.STARTED);

		onStart();

		this.plugin.getEventManager().setCooldown(0L);
	}

	public void end() {

		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> plugin.getEventManager().getEventWorld().getPlayers().forEach(player -> HCFData.sendToSpawnAndReset(player)), 2L);
		
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

		getPlayers().clear();

		setState(EventState.UNANNOUNCED);

		Iterator<UUID> iterator = this.plugin.getEventManager().getSpectators().keySet().iterator();

		while(iterator.hasNext()) {
			UUID spectatorUUID = iterator.next();
			Player spectator = Bukkit.getPlayer(spectatorUUID);

			if(spectator != null) {

				this.plugin.getServer().getScheduler().runTask(this.plugin, () -> HCFData.sendToSpawnAndReset(spectator));
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
