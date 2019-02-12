package secondlife.network.hcfactions.events.sumo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.events.EventCountdownTask;
import secondlife.network.hcfactions.events.KitMapEvent;
import secondlife.network.hcfactions.utilties.CustomLocation;
import secondlife.network.hcfactions.utilties.HCFUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SumoEvent extends KitMapEvent<SumoPlayer> {

	private final Map<UUID, SumoPlayer> players = new HashMap<>();

	@Getter final HashSet<String> fighting = new HashSet<>();
	private final SumoCountdownTask countdownTask = new SumoCountdownTask(this);
	@Getter private WaterCheckTask waterCheckTask;

	public SumoEvent() {
		super("Sumo");
	}

	@Override
	public Map<UUID, SumoPlayer> getPlayers() {
		return players;
	}

	@Override
	public EventCountdownTask getCountdownTask() {
		return countdownTask;
	}

	@Override
	public List<CustomLocation> getSpawnLocations() {
		return Collections.singletonList(this.getPlugin().getSpawnManager().getSumoLocation());
	}

	@Override
	public void onStart() {
		this.waterCheckTask = new WaterCheckTask();
		this.waterCheckTask.runTaskTimer(getPlugin(), 0, 10L);
		selectPlayers();
	}

	@Override
	public Consumer<Player> onJoin() {
		return player -> players.put(player.getUniqueId(), new SumoPlayer(player.getUniqueId(), this));
	}

	@Override
	public Consumer<Player> onDeath() {

		return player -> {

			SumoPlayer data = getPlayer(player);

			if (data == null || data.getFighting() == null) {
				return;
			}

			if(data.getState() == SumoPlayer.SumoState.FIGHTING || data.getState() == SumoPlayer.SumoState.PREPARING) {
				SumoPlayer killerData = data.getFighting();
				Player killer = getPlugin().getServer().getPlayer(killerData.getUuid());

				data.getFightTask().cancel();
				killerData.getFightTask().cancel();

				data.setState(SumoPlayer.SumoState.ELIMINATED);
				killerData.setState(SumoPlayer.SumoState.WAITING);

				HCFUtils.clearPlayer(player);
				HCFData.giveLobbyItems(player);

				HCFUtils.clearPlayer(killer);
				HCFData.giveLobbyItems(killer);

				if (getSpawnLocations().size() == 1) {
					player.teleport(getSpawnLocations().get(0).toBukkitLocation());
					killer.teleport(getSpawnLocations().get(0).toBukkitLocation());
				}

				sendMessage(ChatColor.LIGHT_PURPLE + "(Event) " + ChatColor.RESET + player.getName() + ChatColor.YELLOW + " has been eliminated" + (killer == null ? "." : " by " + ChatColor.GREEN + killer.getName()));

				if (this.getByState(SumoPlayer.SumoState.WAITING).size() == 1) {
					Player winner = Bukkit.getPlayer(this.getByState(SumoPlayer.SumoState.WAITING).get(0));

					for (int i = 0; i <= 2; i++) {
						String announce = ChatColor.LIGHT_PURPLE + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + winner.getName();
						Bukkit.broadcastMessage(announce);
					}

					this.fighting.clear();
					end();
				} else {
					getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), this::selectPlayers, 3 * 20);
				}
			}
		};
	}

	private CustomLocation[] getSumoLocations() {
		CustomLocation[] array = new CustomLocation[2];
		array[0] = this.getPlugin().getSpawnManager().getSumoFirst();
		array[1] = this.getPlugin().getSpawnManager().getSumoSecond();
		return array;
	}

	private void selectPlayers() {
		if (this.getByState(SumoPlayer.SumoState.WAITING).size() == 1) {
			Player winner = Bukkit.getPlayer(this.getByState(SumoPlayer.SumoState.WAITING).get(0));

			for (int i = 0; i <= 2; i++) {
				String announce = ChatColor.LIGHT_PURPLE + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + winner.getName();
				Bukkit.broadcastMessage(announce);
			}

			this.fighting.clear();
			end();
			return;
		}

		Player picked1 = getRandomPlayer();
		Player picked2 = getRandomPlayer();

		if(picked1 == null || picked2 == null) {
			selectPlayers();
			return;
		}

		sendMessage(ChatColor.LIGHT_PURPLE + "(Event) " + ChatColor.YELLOW + "Selecting random players...");

		this.fighting.clear();

		SumoPlayer picked1Data = getPlayer(picked1);
		SumoPlayer picked2Data = getPlayer(picked2);

		picked1Data.setFighting(picked2Data);
		picked2Data.setFighting(picked1Data);

		this.fighting.add(picked1.getName());
		this.fighting.add(picked2.getName());

		HCFUtils.clearPlayer(picked1);
		HCFUtils.clearPlayer(picked2);

		picked1.teleport(getSumoLocations()[0].toBukkitLocation());
		picked2.teleport(getSumoLocations()[1].toBukkitLocation());

		for(Player other : this.getBukkitPlayers()) {
			if(other != null) {
				other.showPlayer(picked1);
				other.showPlayer(picked2);
			}
		}

		for(UUID spectatorUUID : this.getPlugin().getEventManager().getSpectators().keySet()) {
			Player spectator = Bukkit.getPlayer(spectatorUUID);
			if(spectator != null) {
				spectator.showPlayer(picked1);
				spectator.showPlayer(picked2);
			}
		}

		picked1.showPlayer(picked2);
		picked2.showPlayer(picked1);

		sendMessage(ChatColor.YELLOW + "Starting event match. " + ChatColor.GREEN + "(" + picked1.getName() + " vs " + picked2.getName() + ")");

		BukkitTask task = new SumoFightTask(picked1, picked2, picked1Data, picked2Data).runTaskTimer(getPlugin(), 0, 20);

		picked1Data.setFightTask(task);
		picked2Data.setFightTask(task);
	}

	private Player getRandomPlayer() {

		if(getByState(SumoPlayer.SumoState.WAITING).size() == 0) {
			return null;
		}

		List<UUID> waiting = getByState(SumoPlayer.SumoState.WAITING);

		Collections.shuffle(waiting);

		UUID uuid = waiting.get(ThreadLocalRandom.current().nextInt(waiting.size()));

		getPlayer(uuid).setState(SumoPlayer.SumoState.PREPARING);

		return getPlugin().getServer().getPlayer(uuid);
	}

	public List<UUID> getByState(SumoPlayer.SumoState state) {
		return players.values().stream().filter(player -> player.getState() == state).map(SumoPlayer::getUuid).collect(Collectors.toList());
	}

	/**
	 * To ensure that the fight doesn't go on forever and to
	 * let the players know how much time they have left.
	 */
	@Getter
	@RequiredArgsConstructor
	public class SumoFightTask extends BukkitRunnable {
		private final Player player;
		private final Player other;

		private final SumoPlayer playerSumo;
		private final SumoPlayer otherSumo;

		private int time = 90;

		@Override
		public void run() {

			if (player == null || other == null || !player.isOnline() || !other.isOnline()) {
				cancel();
				return;
			}

			if (time == 90) {
				HCFUtils.sendMessage(ChatColor.YELLOW + "The match starts in " + ChatColor.LIGHT_PURPLE + 3 + ChatColor.YELLOW + "...", player, other);
			} else if (time == 89) {
				HCFUtils.sendMessage(ChatColor.YELLOW + "The match starts in " + ChatColor.LIGHT_PURPLE + 2 + ChatColor.YELLOW + "...", player, other);
			} else if (time == 88) {
				HCFUtils.sendMessage(ChatColor.YELLOW + "The match starts in " + ChatColor.LIGHT_PURPLE + 1 + ChatColor.YELLOW + "...", player, other);
			} else if (time == 87) {
				HCFUtils.sendMessage(ChatColor.GREEN + "The match has started, good luck!", player, other);
				this.otherSumo.setState(SumoPlayer.SumoState.FIGHTING);
				this.playerSumo.setState(SumoPlayer.SumoState.FIGHTING);
			} else if (time <= 0) {
				List<Player> players = Arrays.asList(player, other);
				Player winner = players.get(ThreadLocalRandom.current().nextInt(players.size()));
				players.stream().filter(pl -> !pl.equals(winner)).forEach(pl -> onDeath().accept(pl));

				cancel();
				return;
			}

			if (Arrays.asList(30, 25, 20, 15, 10).contains(time)) {
				HCFUtils.sendMessage(ChatColor.YELLOW + "The match ends in " + ChatColor.LIGHT_PURPLE + time + ChatColor.YELLOW + "...", player, other);
			} else if (Arrays.asList(5, 4, 3, 2, 1).contains(time)) {
				HCFUtils.sendMessage(ChatColor.YELLOW + "The match is ending in " + ChatColor.LIGHT_PURPLE + time + ChatColor.YELLOW + "...", player, other);
			}

			time--;
		}
	}

	@Getter
	@RequiredArgsConstructor
	public class WaterCheckTask extends BukkitRunnable {
		@Override
		public void run() {

			if (getPlayers().size() <= 1) {
				return;
			}

			getBukkitPlayers().forEach(player -> {

				if (getPlayer(player) != null && getPlayer(player).getState() != SumoPlayer.SumoState.FIGHTING) {
					return;
				}

				Block legs = player.getLocation().getBlock();
				Block head = legs.getRelative(BlockFace.UP);
				if (legs.getType() == Material.WATER || legs.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
					onDeath().accept(player);
				}
			});
		}
	}
}
