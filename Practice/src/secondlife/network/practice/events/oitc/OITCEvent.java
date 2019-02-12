package secondlife.network.practice.events.oitc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import secondlife.network.practice.events.EventCountdownTask;
import secondlife.network.practice.events.PracticeEvent;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.utilties.CustomLocation;
import secondlife.network.practice.utilties.PlayerUtil;
import secondlife.network.vituz.utilties.ItemBuilder;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OITCEvent extends PracticeEvent<OITCPlayer> {

	private final Map<UUID, OITCPlayer> players = new HashMap<>();

	@Getter private OITCGameTask gameTask = null;
	private final OITCCountdownTask countdownTask = new OITCCountdownTask(this);
	private List<CustomLocation> respawnLocations;

	public OITCEvent() {
		super("OITC");
	}

	@Override
	public Map<UUID, OITCPlayer> getPlayers() {
		return players;
	}

	@Override
	public EventCountdownTask getCountdownTask() {
		return countdownTask;
	}

	@Override
	public List<CustomLocation> getSpawnLocations() {
		return Collections.singletonList(this.getPlugin().getSpawnManager().getOitcLocation());
	}

	@Override
	public void onStart() {
		this.respawnLocations = new ArrayList<>();
		this.gameTask = new OITCGameTask();
		this.gameTask.runTaskTimerAsynchronously(getPlugin(), 0, 20L);
	}

	@Override
	public Consumer<Player> onJoin() {
		return player -> players.put(player.getUniqueId(), new OITCPlayer(player.getUniqueId(), this));
	}

	@Override
	public Consumer<Player> onDeath() {

		return player -> {

			OITCPlayer data = getPlayer(player);

			if (data == null) {
				return;
			}

			if(data.getState() == OITCPlayer.OITCState.WAITING) {
				return;
			}

			if(data.getState() == OITCPlayer.OITCState.FIGHTING || data.getState() == OITCPlayer.OITCState.PREPARING || data.getState() == OITCPlayer.OITCState.RESPAWNING) {

				String deathMessage = CC.SECONDARY + "(Event) " + CC.RESET + player.getName() + "(" + data.getScore() + ")" + CC.PRIMARY + " has been eliminated from the game.";

				if(data.getLastKiller() != null) {

					OITCPlayer killerData = data.getLastKiller();
					Player killer = getPlugin().getServer().getPlayer(killerData.getUuid());

					int count = killerData.getScore() + 1;
					killerData.setScore(count);

					killer.getInventory().setItem(6, new ItemBuilder(Material.GLOWSTONE_DUST).name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Kills").lore((killerData.getScore() == 0 ? 1 : killerData.getScore()) + "").build());
					if(killer.getInventory().contains(Material.ARROW)) {
						killer.getInventory().getItem(8).setAmount(killer.getInventory().getItem(8).getAmount() + 2);
					} else {
						killer.getInventory().setItem(8, new ItemStack(Material.ARROW, 2));
					}
					killer.updateInventory();

					killer.playSound(killer.getLocation(), Sound.NOTE_PLING, 1F, 1F);

					FireworkEffect fireworkEffect = FireworkEffect.builder().withColor(Color.fromRGB(127, 56, 56)).withFade(Color.fromRGB(127, 56, 56)).with(FireworkEffect.Type.BALL).build();
					PlayerUtil.sendFirework(fireworkEffect, player.getLocation().add(0, 1.5, 0));

					PracticeData playerData = PracticeData.getByName(killer.getName());
					playerData.setOitcEventKills(playerData.getOitcEventKills() + 1);

					data.setLastKiller(null);

					deathMessage = CC.SECONDARY + "(Event) " + CC.RESET + player.getName() + "(" + data.getScore() + ")" + CC.PRIMARY + " has been killed" + (killer == null ? "." : " by " + ChatColor.GREEN + killer.getName() + "(" + count + ")");

					if (count == 25) {

						PracticeData winnerData = PracticeData.getByName(killer.getName());
						winnerData.setOitcEventWins(winnerData.getOitcEventWins() + 1);

						for (int i = 0; i <= 2; i++) {
							String announce = CC.SECONDARY + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + killer.getName();
							Bukkit.broadcastMessage(announce);
						}

						this.gameTask.cancel();
						end();
					}
				}

				if (data.getLastKiller() == null) {
					// Respawn the player
					data.setLives((data.getLives() - 1));

					PracticeData playerData = PracticeData.getByName(player.getName());
					playerData.setOitcEventDeaths(playerData.getOitcEventDeaths() + 1);

					if(data.getLives() == 0) {

						playerData.setOitcEventLosses(playerData.getOitcEventLosses() + 1);
                        getPlayers().remove(player.getUniqueId());

                        player.sendMessage(CC.SECONDARY + "(Event) " + CC.PRIMARY + "You have been eliminated from the game.");

                        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
                            PracticeData.sendToSpawnAndReset(player);
                            if(getPlayers().size() >= 2) {
                               getPlugin().getEventManager().addSpectatorOITC(player, PracticeData.getByName(player.getName()), OITCEvent.this);
                            }
                        });
                    } else {
                        BukkitTask respawnTask = new RespawnTask(player, data).runTaskTimerAsynchronously(getPlugin(), 0, 20);
                        data.setRespawnTask(respawnTask);
                    }
				}

				sendMessage(deathMessage);
			}
		};
	}

	public void teleportNextLocation(Player player) {
	    player.teleport(getGameLocations().remove(ThreadLocalRandom.current().nextInt(getGameLocations().size())).toBukkitLocation());
	}

	private List<CustomLocation> getGameLocations() {

		if(this.respawnLocations != null && this.respawnLocations.size() == 0) {
			this.respawnLocations.addAll(this.getPlugin().getSpawnManager().getOitcSpawnpoints());
		}

		return this.respawnLocations;
	}

	private void giveRespawnItems(Player player, OITCPlayer oitcPlayer) {

		this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
            PlayerUtil.clearPlayer(player);
            player.getInventory().setItem(0, new ItemBuilder(Material.WOOD_SWORD).name(ChatColor.GREEN + "Wood Sword").build());
            player.getInventory().setItem(1, new ItemBuilder(Material.BOW).name(ChatColor.GREEN + "Bow").build());
            player.getInventory().setItem(6, new ItemBuilder(Material.GLOWSTONE_DUST).name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Kills").lore((oitcPlayer.getScore() == 0 ? 1 : oitcPlayer.getScore()) + "").build());
            player.getInventory().setItem(7, new ItemBuilder(Material.REDSTONE).name(ChatColor.RED.toString() + ChatColor.BOLD + "Lives").lore("" + oitcPlayer.getLives()).build());
            player.getInventory().setItem(8, new ItemStack(Material.ARROW));
			player.updateInventory();
        });
	}

	private Player getWinnerPlayer() {

		if(getByState(OITCPlayer.OITCState.FIGHTING).size() == 0) {
			return null;
		}

		List<OITCPlayer> fighting = this.sortedScores();
		return getPlugin().getServer().getPlayer(fighting.get(0).getUuid());
	}

	private List<UUID> getByState(OITCPlayer.OITCState state) {
		return players.values().stream().filter(player -> player.getState() == state).map(OITCPlayer::getUuid).collect(Collectors.toList());
	}

    @Getter
    @RequiredArgsConstructor
    public class RespawnTask extends BukkitRunnable {

	    private final Player player;
	    private final OITCPlayer oitcPlayer;
        private int time = 5;

        @Override
        public void run() {

            if(oitcPlayer.getLives() == 0) {
                cancel();
                return;
            }

            if(time > 0) {
				player.sendMessage(CC.SECONDARY + "(Event) " + CC.PRIMARY + "Respawning in " + time + "...");
			}

            if(time == 5) {
                getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
                    PlayerUtil.clearPlayer(player);
                    getBukkitPlayers().forEach(member -> member.hidePlayer(player));
					getBukkitPlayers().forEach(player::hidePlayer);
					player.setGameMode(GameMode.SPECTATOR);
                });

                oitcPlayer.setState(OITCPlayer.OITCState.RESPAWNING);

            } else if(time <= 0) {
                player.sendMessage(CC.SECONDARY + "(Event) " + CC.PRIMARY + "Respawning...");
                player.sendMessage(CC.SECONDARY + "(Event) " + ChatColor.RED.toString() + ChatColor.BOLD + oitcPlayer.getLives() + " " + (oitcPlayer.getLives() == 1 ? "LIFE" : "LIVES") + " REMAINING");


                getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), () -> {
                    giveRespawnItems(player, oitcPlayer);
                    player.teleport(getGameLocations().remove(ThreadLocalRandom.current().nextInt(getGameLocations().size())).toBukkitLocation());
					//((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
                    //((CraftPlayer) player).getHandle().playerConnection.player.setFakingDeath(false);
                    getBukkitPlayers().forEach(member -> member.showPlayer(player));
					getBukkitPlayers().forEach(player::showPlayer);
                }, 2L);

                oitcPlayer.setState(OITCPlayer.OITCState.FIGHTING);

                cancel();
            }

            time--;

        }
    }

	/**
	 * To ensure that the fight doesn't go on forever and to
	 * let the players know how much time they have left.
	 */
	@Getter
	@RequiredArgsConstructor
	public class OITCGameTask extends BukkitRunnable {

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

				for(OITCPlayer player : getPlayers().values()) {
					player.setScore(0);
					player.setLives(5);
					player.setState(OITCPlayer.OITCState.FIGHTING);
				}

				for(Player player : getBukkitPlayers()) {

					OITCPlayer oitcPlayer = getPlayer(player.getUniqueId());

					if(oitcPlayer != null) {
						teleportNextLocation(player);
						giveRespawnItems(player, oitcPlayer);
					}
				}

			} else if (time <= 0) {

				Player winner = getWinnerPlayer();

				if(winner != null) {

					PracticeData winnerData = PracticeData.getByName(winner.getName());
					winnerData.setOitcEventWins(winnerData.getOitcEventWins() + 1);

					for (int i = 0; i <= 2; i++) {
						String announce = CC.SECONDARY + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + winner.getName();
						Bukkit.broadcastMessage(announce);
					}
				}

                gameTask.cancel();
                end();
				cancel();
				return;
			}

			if (getByState(OITCPlayer.OITCState.FIGHTING).size() == 1 || getPlayers().size() == 1) {
				Player winner = Bukkit.getPlayer(getByState(OITCPlayer.OITCState.FIGHTING).get(0));

				PracticeData winnerData = PracticeData.getByName(winner.getName());
				winnerData.setOitcEventWins(winnerData.getOitcEventWins() + 1);

				for (int i = 0; i <= 2; i++) {
					String announce = CC.SECONDARY + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + winner.getName();
					Bukkit.broadcastMessage(announce);
				}

				cancel();
				end();
			}

			if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(time)) {
				PlayerUtil.sendMessage(ChatColor.YELLOW + "The game ends in " + CC.SECONDARY + time + ChatColor.YELLOW + "...", getBukkitPlayers());
			} else if (Arrays.asList(5, 4, 3, 2, 1).contains(time)) {
				PlayerUtil.sendMessage(ChatColor.YELLOW + "The game is ending in " + CC.SECONDARY + time + ChatColor.YELLOW + "...", getBukkitPlayers());
			}

			time--;
		}
	}

	public List<OITCPlayer> sortedScores() {
		List<OITCPlayer> list = new ArrayList<>(this.players.values());
		list.sort(new SortComparator().reversed());
		return list;
	}

	private class SortComparator implements Comparator<OITCPlayer> {

		@Override public int compare(OITCPlayer p1, OITCPlayer p2) {
			return Integer.compare(p1.getScore(), p2.getScore());
		}
	}
}
