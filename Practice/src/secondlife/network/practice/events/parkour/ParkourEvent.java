package secondlife.network.practice.events.parkour;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.practice.events.EventCountdownTask;
import secondlife.network.practice.events.PracticeEvent;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.BlockUtil;
import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.utilties.CustomLocation;
import secondlife.network.practice.utilties.PlayerUtil;
import secondlife.network.vituz.utilties.ItemBuilder;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ParkourEvent extends PracticeEvent<ParkourPlayer> {

	private final Map<UUID, ParkourPlayer> players = new HashMap<>();

	@Getter private ParkourGameTask gameTask = null;
	private final ParkourCountdownTask countdownTask = new ParkourCountdownTask(this);
	@Getter private WaterCheckTask waterCheckTask;
	private List<UUID> visibility;

	public ParkourEvent() {
		super("Parkour");
	}

	@Override
	public Map<UUID, ParkourPlayer> getPlayers() {
		return players;
	}

	@Override
	public EventCountdownTask getCountdownTask() {
		return countdownTask;
	}

	@Override
	public List<CustomLocation> getSpawnLocations() {
		return Collections.singletonList(this.getPlugin().getSpawnManager().getParkourLocation());
	}

	@Override
	public void onStart() {
		this.gameTask = new ParkourGameTask();
		this.gameTask.runTaskTimerAsynchronously(getPlugin(), 0, 20L);
		this.waterCheckTask = new WaterCheckTask();
		this.waterCheckTask.runTaskTimer(getPlugin(), 0, 10L);
		this.visibility = new ArrayList<>();
	}

	@Override
	public Consumer<Player> onJoin() {
		return player -> players.put(player.getUniqueId(), new ParkourPlayer(player.getUniqueId(), this));
	}

	@Override
	public Consumer<Player> onDeath() {

		return player -> {
			String message = CC.SECONDARY + "(Event) " + ChatColor.RESET + player.getName() + CC.PRIMARY + " has left the game.";
			sendMessage(message);

			PracticeData playerData = PracticeData.getByName(player.getName());
			playerData.setParkourEventLosses(playerData.getParkourEventLosses() + 1);
		};
	}

	public void toggleVisibility(Player player) {

		if(this.visibility.contains(player.getUniqueId())) {

			for(Player playing : this.getBukkitPlayers()) {
				player.showPlayer(playing);
			}

			this.visibility.remove(player.getUniqueId());
			player.sendMessage(ChatColor.GREEN + "You are now showing players.");
			return;
		}

		for(Player playing : this.getBukkitPlayers()) {
			player.hidePlayer(playing);
		}

		this.visibility.add(player.getUniqueId());
		player.sendMessage(ChatColor.GREEN + "You are now hiding players.");

	}

	private void teleportToSpawnOrCheckpoint(Player player) {

		ParkourPlayer parkourPlayer = this.getPlayer(player.getUniqueId());

		if(parkourPlayer != null && parkourPlayer.getLastCheckpoint() != null) {
			player.teleport(parkourPlayer.getLastCheckpoint().toBukkitLocation());
			player.sendMessage(CC.SECONDARY + "(Event) " + CC.PRIMARY + "Teleporting back to last checkpoint.");
			return;
		}

		player.sendMessage(CC.SECONDARY + "(Event) " + CC.PRIMARY + "Teleporting back to the beginning.");
	    player.teleport(this.getPlugin().getSpawnManager().getParkourGameLocation().toBukkitLocation());
	}

	private void giveItems(Player player) {

		this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
			PlayerUtil.clearPlayer(player);
            player.getInventory().setItem(0, new ItemBuilder(Material.FIREBALL).name(ChatColor.GREEN.toString() + ChatColor.BOLD + "Toggle Visibility").build());
			player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).name(ChatColor.RED.toString() + ChatColor.BOLD + "Leave Event").build());
			player.updateInventory();
        });
	}

	private Player getRandomPlayer() {

		if(getByState(ParkourPlayer.ParkourState.INGAME).size() == 0) {
			return null;
		}

		List<UUID> fighting = getByState(ParkourPlayer.ParkourState.INGAME);

		Collections.shuffle(fighting);

		UUID uuid = fighting.get(ThreadLocalRandom.current().nextInt(fighting.size()));

		return getPlugin().getServer().getPlayer(uuid);
	}

	public List<UUID> getByState(ParkourPlayer.ParkourState state) {
		return players.values().stream().filter(player -> player.getState() == state).map(ParkourPlayer::getUuid).collect(Collectors.toList());
	}

	/**
	 * To ensure that the fight doesn't go on forever and to
	 * let the players know how much time they have left.
	 */
	@Getter
	@RequiredArgsConstructor
	public class ParkourGameTask extends BukkitRunnable {

		private int time = 303;

		@Override
		public void run() {

			if (time == 303) {
				PlayerUtil.sendMessage(ChatColor.YELLOW + "The game starts in " + CC.SECONDARY + 3 + ChatColor.YELLOW + "...", getBukkitPlayers());
			} else if (time == 302) {
				PlayerUtil.sendMessage(ChatColor.YELLOW + "The game starts in " + CC.SECONDARY + 2 + ChatColor.YELLOW + "...", getBukkitPlayers());
			} else if (time == 301) {
				PlayerUtil.sendMessage(ChatColor.YELLOW + "The game starts in " + CC.SECONDARY + 1 + ChatColor.YELLOW + "...", getBukkitPlayers());
			} else if (time == 300) {
				PlayerUtil.sendMessage(ChatColor.GREEN + "The game has started, good luck!", getBukkitPlayers());

				for(ParkourPlayer player : getPlayers().values()) {
					player.setLastCheckpoint(null);
					player.setState(ParkourPlayer.ParkourState.INGAME);
					player.setCheckpointId(0);
				}

				for(Player player : getBukkitPlayers()) {
					teleportToSpawnOrCheckpoint(player);
					giveItems(player);
				}

			} else if (time <= 0) {

				Player winner = getRandomPlayer();

				if(winner != null) {

					PracticeData winnerData = PracticeData.getByName(winner.getName());
					winnerData.setParkourEventWins(winnerData.getParkourEventWins() + 1);

					for (int i = 0; i <= 2; i++) {
						String announce = CC.SECONDARY + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + winner.getName();
						Bukkit.broadcastMessage(announce);
					}
				}

                end();
				cancel();
				return;
			}

			if(getPlayers().size() == 1) {
				Player winner = Bukkit.getPlayer(getByState(ParkourPlayer.ParkourState.INGAME).get(0));

				if(winner != null) {

					PracticeData winnerData = PracticeData.getByName(winner.getName());
					winnerData.setParkourEventWins(winnerData.getParkourEventWins() + 1);

					for (int i = 0; i <= 2; i++) {
						String announce = CC.SECONDARY + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + winner.getName();
						Bukkit.broadcastMessage(announce);
					}
				}

				end();
				cancel();
				return;
			}


			if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(time)) {
				PlayerUtil.sendMessage(ChatColor.YELLOW + "The game ends in " + CC.SECONDARY + time + ChatColor.YELLOW + "...", getBukkitPlayers());
			} else if (Arrays.asList(5, 4, 3, 2, 1).contains(time)) {
				PlayerUtil.sendMessage(ChatColor.YELLOW + "The game is ending in " + CC.SECONDARY + time + ChatColor.YELLOW + "...", getBukkitPlayers());
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

				if (getPlayer(player) != null && getPlayer(player).getState() != ParkourPlayer.ParkourState.INGAME) {
					return;
				}

				if (BlockUtil.isStandingOn(player, Material.WATER) || BlockUtil.isStandingOn(player, Material.STATIONARY_WATER)) {
					teleportToSpawnOrCheckpoint(player);
				}

				else if (BlockUtil.isStandingOn(player, Material.STONE_PLATE) || BlockUtil.isStandingOn(player, Material.IRON_PLATE) || BlockUtil.isStandingOn(player, Material.WOOD_PLATE)) {
					ParkourPlayer parkourPlayer = getPlayer(player.getUniqueId());

					if(parkourPlayer != null) {

						boolean checkpoint = false;

						if(parkourPlayer.getLastCheckpoint() == null) {
							checkpoint = true;
							parkourPlayer.setLastCheckpoint(CustomLocation.fromBukkitLocation(player.getLocation()));
						}
						else if(parkourPlayer.getLastCheckpoint() != null && !BlockUtil.isSameLocation(player.getLocation(), parkourPlayer.getLastCheckpoint().toBukkitLocation())) {
							checkpoint = true;
							parkourPlayer.setLastCheckpoint(CustomLocation.fromBukkitLocation(player.getLocation()));
						}

						if(checkpoint) {
							parkourPlayer.setCheckpointId( parkourPlayer.getCheckpointId() + 1 );
							player.sendMessage(CC.SECONDARY + "(Event) " + CC.PRIMARY + "Checkpoint #" + parkourPlayer.getCheckpointId() + " has been set.");
						}
					}
				} else if(BlockUtil.isStandingOn(player, Material.GOLD_PLATE)) {
					for (int i = 0; i <= 2; i++) {
						String announce = CC.SECONDARY + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + player.getName();
						Bukkit.broadcastMessage(announce);
					}

					end();
					cancel();
				}
			});
		}
	}
}
