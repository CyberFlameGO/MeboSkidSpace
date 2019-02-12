package secondlife.network.practice.managers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.arena.Arena;
import secondlife.network.practice.kit.Kit;
import secondlife.network.practice.match.Match;
import secondlife.network.practice.match.MatchTeam;
import secondlife.network.practice.party.Party;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.queue.QueueEntry;
import secondlife.network.practice.queue.QueueType;
import secondlife.network.practice.utilties.CC;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {

	private final Map<UUID, QueueEntry> queued = new ConcurrentHashMap<>();
	private final Map<UUID, Long> playerQueueTime = new HashMap<>();

	private final Practice plugin = Practice.getInstance();

	@Getter
	@Setter
	private boolean rankedEnabled = true;

	public QueueManager() {
		this.plugin.getServer().getScheduler().runTaskTimer(this.plugin,
				() -> this.queued.forEach((key, value) -> {
					if (value.isParty()) {
						this.findMatch(this.plugin.getPartyManager().getParty(key), value.getKitName(),
								value.getElo(), value.getQueueType());
					} else {
						this.findMatch(this.plugin.getServer().getPlayer(key), value.getKitName(),
								value.getElo(), value.getQueueType());
					}
				}), 20L, 20L);
	}

	public void addPlayerToQueue(Player player, PracticeData playerData, String kitName, QueueType type) {
		if (type != QueueType.UNRANKED && !this.rankedEnabled) {
			player.sendMessage(CC.RED + "Ranked is currently disabled until the server restarts.");
			player.closeInventory();
			return;
		}

		playerData.setPlayerState(PlayerState.QUEUE);

		int elo = type == QueueType.RANKED ? playerData.getElo(kitName) : type == QueueType.PREMIUM ? playerData.getPremiumElo() : 0;

		QueueEntry entry = new QueueEntry(type, kitName, elo, false);

		/*if(playerData == null) {
			System.out.print("PLAYERDATA");
			return;
		}

		if(playerData.getUniqueId() == null) {
			System.out.print("UNIQUEID");
			return;
		}

		if(entry == null) {
			System.out.print("ENTRY");
			return;
		}*/

		this.queued.put(player.getUniqueId(), entry);

		this.giveQueueItems(player);

		player.sendMessage(type != QueueType.UNRANKED ?
				CC.PRIMARY + "You were added to the " + CC.SECONDARY + type.getName() + " " + kitName + CC.PRIMARY +
						" " +
						"queue" +
						" with " + CC.SECONDARY + elo + CC.PRIMARY + " elo." :
				CC.PRIMARY + "You were added to the " + CC.SECONDARY + "Unranked " + kitName + CC.PRIMARY + " queue.");

		this.playerQueueTime.put(player.getUniqueId(), System.currentTimeMillis());
	}

	private void giveQueueItems(Player player) {
		player.closeInventory();
		player.getInventory().setContents(this.plugin.getItemManager().getQueueItems());
		player.updateInventory();
	}

	public QueueEntry getQueueEntry(UUID uuid) {
		return this.queued.get(uuid);
	}

	public long getPlayerQueueTime(UUID uuid) {
		return this.playerQueueTime.get(uuid);
	}

	public int getQueueSize(String ladder, QueueType type) {
		return (int) this.queued.entrySet().stream().filter(entry -> entry.getValue().getQueueType() == type)
				.filter(entry -> entry.getValue().getKitName().equals(ladder)).count();
	}

	private boolean findMatch(Player player, String kitName, int elo, QueueType type) {
		long queueTime = System.currentTimeMillis() - this.playerQueueTime.get(player.getUniqueId());

		PracticeData playerData = PracticeData.getByName(player.getName());

		int eloRange = playerData.getEloRange();
		int pingRange = -1;
		int seconds = Math.round(queueTime / 1000L);
		if (seconds > 5 && type != QueueType.UNRANKED) {
			if (pingRange != -1) {
				pingRange += (seconds - 5) * 25;
			}

			if (eloRange != -1) {
				eloRange += seconds * 50;
				if (eloRange >= 3000) {
					eloRange = 3000;
				}
			}
		}

		if (eloRange == -1) {
			eloRange = Integer.MAX_VALUE;
		}
		if (pingRange == -1) {
			pingRange = Integer.MAX_VALUE;
		}

		int ping = 0;

		for (UUID opponent : this.queued.keySet()) {
			if (opponent.equals(player.getUniqueId())) {
				continue;
			}

			QueueEntry queueEntry = this.queued.get(opponent);

			if (!queueEntry.getKitName().equals(kitName)) {
				continue;
			}

			if (queueEntry.getQueueType() != type) {
				continue;
			}

			if (queueEntry.isParty()) {
				continue;
			}

			Player opponentPlayer = this.plugin.getServer().getPlayer(opponent);
			PracticeData opponentData = PracticeData.getByName(opponentPlayer.getName());

			if (opponentData.getPlayerState() == PlayerState.FIGHTING) {
				continue;
			}

			if (playerData.getPlayerState() == PlayerState.FIGHTING) {
				continue;
			}

			int eloDiff = Math.abs(queueEntry.getElo() - elo);

			if (type.isRanked()) {
				if (eloDiff > eloRange) {
					continue;
				}

				long opponentQueueTime = System.currentTimeMillis() - this.playerQueueTime.get(opponentPlayer.getUniqueId());

				int opponentEloRange = -1;
				int opponentPingRange = -1;
				int opponentSeconds = Math.round(opponentQueueTime / 1000L);

				if (opponentSeconds > 5) {
					if (opponentPingRange != -1) {
						opponentPingRange += (opponentSeconds - 5) * 25;
					}

					if (opponentEloRange != -1) {
						opponentEloRange += opponentSeconds * 50;
						if (opponentEloRange >= 3000) {
							opponentEloRange = 3000;
						}
					}
				}

				if (opponentEloRange == -1) {
					opponentEloRange = Integer.MAX_VALUE;
				}

				if (opponentPingRange == -1) {
					opponentPingRange = Integer.MAX_VALUE;
				}

				if (eloDiff > opponentEloRange) {
					continue;
				}

				int pingDiff = Math.abs(0 - ping);

				if (type == QueueType.RANKED) {
					if (pingDiff > opponentPingRange) {
						continue;
					}
					if (pingDiff > pingRange) {
						continue;
					}
				}
			}

			Kit kit = this.plugin.getKitManager().getKit(kitName);

			Arena arena = this.plugin.getArenaManager().getRandomArena(kit);

			String playerFoundMatchMessage;
			String matchedFoundMatchMessage;
			String playerArenaMessage;
			String matchedArenaMessage;

			if (type.isRanked()) {
				playerFoundMatchMessage = CC.PRIMARY + "Found " + type.getName().toLowerCase() + " match: " + CC
						.GREEN +
						player.getName() + " (" + elo + " elo)" + CC.PRIMARY
						+ " vs. " + CC.RED + opponentPlayer.getName() + " (" +
						this.queued.get(opponentPlayer.getUniqueId()).getElo() + " elo)";
				matchedFoundMatchMessage = CC.PRIMARY + "Found " + type.getName().toLowerCase() + " match: " +
						CC.GREEN +
						opponentPlayer.getName() + " (" +
						this.queued.get(opponentPlayer.getUniqueId()).getElo()
						+ " elo)" + CC.PRIMARY + " vs. " + CC.RED + player.getName() + " (" + elo
						+ " elo)";
			} else {
				playerFoundMatchMessage = CC.PRIMARY + "Found unranked match: " + CC.GREEN + player.getName() +
						CC.PRIMARY
						+ " vs. " + CC.RED + opponentPlayer.getName();
				matchedFoundMatchMessage = CC.PRIMARY + "Found unranked match: " + CC.GREEN + opponentPlayer.getName
						() +
						CC.PRIMARY + " vs. " + CC.RED + player.getName();
			}

			if (type == QueueType.PREMIUM) {
				playerData.setPremiumMatchesPlayed(playerData.getPremiumMatchesPlayed() + 1);
				opponentData.setPremiumMatchesPlayed(opponentData.getPremiumMatchesPlayed() + 1);
			}

			playerArenaMessage = CC.PRIMARY + "You are playing on arena " + CC.SECONDARY + arena.getName() + CC.PRIMARY + ".";
			matchedArenaMessage = CC.PRIMARY + "You are playing on arena " + CC.SECONDARY + arena.getName() + CC.PRIMARY + ".";

			player.sendMessage(playerFoundMatchMessage);
			opponentPlayer.sendMessage(matchedFoundMatchMessage);

			player.sendMessage(playerArenaMessage);
			opponentPlayer.sendMessage(matchedArenaMessage);

			MatchTeam teamA = new MatchTeam(player.getUniqueId(), Collections.singletonList(player.getUniqueId()), 0);
			MatchTeam teamB = new MatchTeam(opponentPlayer.getUniqueId(), Collections.singletonList(opponentPlayer.getUniqueId()), 1);

			Match match = new Match(arena, kit, type, teamA, teamB);

			this.plugin.getMatchManager().createMatch(match);

			this.queued.remove(player.getUniqueId());
			this.queued.remove(opponentPlayer.getUniqueId());

			this.playerQueueTime.remove(player.getUniqueId());

			return true;
		}

		return false;
	}

	public void removePlayerFromQueue(Player player) {
		QueueEntry entry = this.queued.get(player.getUniqueId());

		if(entry == null) return;

		this.queued.remove(player.getUniqueId());

		PracticeData.sendToSpawnAndReset(player);

		player.sendMessage(CC.PRIMARY + "You were removed from the " + CC.SECONDARY + entry.getQueueType().getName()
				+ " " + entry.getKitName() + CC.PRIMARY + " queue.");
	}

	public void addPartyToQueue(Player leader, Party party, String kitName, QueueType type) {
		if (type.isRanked() && !this.rankedEnabled) {
			leader.sendMessage(CC.RED + "Ranked is currently disabled until the server restarts.");
			leader.closeInventory();
		} else if (party.getMembers().size() != 2) {
			leader.sendMessage(CC.RED + "You can only join the queue with 2 players in your party.");
			leader.closeInventory();
		} else {
			party.getMembers().stream().map(PracticeData::getByKurac)
					.forEach(member -> member.setPlayerState(PlayerState.QUEUE));

			PracticeData playerData = PracticeData.getByName(leader.getName());

			int elo = type.isRanked() ? playerData.getPartyElo(kitName) : -1;

			this.queued.put(leader.getUniqueId(), new QueueEntry(type, kitName, elo, true));

			this.giveQueueItems(leader);

			party.broadcast(type.isRanked() ?
					CC.PRIMARY + "Your party was added to the " + type.getName().toLowerCase() + " " + CC.SECONDARY
							+ kitName + CC.PRIMARY + " queue with " + CC.SECONDARY + elo + CC.PRIMARY + " elo." :
					CC.PRIMARY + "Your party was added to the unranked " + CC.SECONDARY + kitName + CC.PRIMARY +
							" queue.");

			this.playerQueueTime.put(party.getLeader(), System.currentTimeMillis());

			this.findMatch(party, kitName, elo, type);
		}
	}

	private void findMatch(Party partyA, String kitName, int elo, QueueType type) {
		if(!this.playerQueueTime.containsKey(partyA.getLeader())) {
			return;
		}

		long queueTime = System.currentTimeMillis() - this.playerQueueTime.get(partyA.getLeader());

		PracticeData playerData = PracticeData.getByName(Bukkit.getPlayer(partyA.getLeader()).getName());

		if(playerData == null) {
			return;
		}

		// Increase elo range by 50 every second after 5 seconds
		int eloRange = playerData.getEloRange();
		int seconds = Math.round(queueTime / 1000L);
		if (seconds > 5 && type.isRanked()) {
			eloRange += seconds * 50;
			if (eloRange >= 1000) {
				eloRange = 1000;
			}
			//partyA.broadcast(CC.SECONDARY + "Searching in ELO range " + CC.PRIMARY + "[" + (elo - eloRange / 2) + " -> " + (elo + eloRange / 2) + "]");
		}

		int finalEloRange = eloRange;
		UUID opponent = this.queued.entrySet().stream()
				.filter(entry -> entry.getKey() != partyA.getLeader())
				.filter(entry -> PracticeData.getByKurac(entry.getKey()).getPlayerState() == PlayerState.QUEUE)
				.filter(entry -> entry.getValue().isParty())
				.filter(entry -> entry.getValue().getQueueType() == type)
				.filter(entry -> !type.isRanked() || Math.abs(entry.getValue().getElo() - elo) < finalEloRange)
				.filter(entry -> entry.getValue().getKitName().equals(kitName))
				.map(Map.Entry::getKey)
				.findFirst().orElse(null);

		if (opponent == null) {
			return;
		}

		PracticeData opponentData = PracticeData.getByName(Bukkit.getPlayer(opponent).getName());


		if (opponentData.getPlayerState() == PlayerState.FIGHTING) {
			return;
		}

		if (playerData.getPlayerState() == PlayerState.FIGHTING) {
			return;
		}


		Player leaderA = this.plugin.getServer().getPlayer(partyA.getLeader());
		Player leaderB = this.plugin.getServer().getPlayer(opponent);

		Party partyB = this.plugin.getPartyManager().getParty(opponent);

		Kit kit = this.plugin.getKitManager().getKit(kitName);

		Arena arena = this.plugin.getArenaManager().getRandomArena(kit);

		String partyAFoundMatchMessage;
		String partyBFoundMatchMessage;

		if (type.isRanked()) {
			partyAFoundMatchMessage = CC.PRIMARY + "Found ranked match: " + CC.GREEN + leaderA.getName() +
					"'s party (" +
					elo + " elo)" + CC.PRIMARY + " vs. "
					+ CC.RED + leaderB.getName() + "'s Party (" +
					this.queued.get(leaderB.getUniqueId()).getElo() + " elo)";
			partyBFoundMatchMessage = CC.PRIMARY + "Found ranked match: " + CC.GREEN + leaderB.getName() + "'s party ("
					+ this.queued.get(leaderB.getUniqueId()).getElo() + " elo)" + CC.PRIMARY +
					" vs. " +
					CC.RED + leaderA.getName() + "'s Party (" + elo + " elo)";
		} else {
			partyAFoundMatchMessage = CC.PRIMARY + "Found unranked match: " + CC.GREEN + leaderA.getName() +
					"'s party" +
					CC.PRIMARY + " vs. "
					+ CC.RED + leaderB.getName() + "'s party";
			partyBFoundMatchMessage = CC.PRIMARY + "Found unranked match: " + CC.GREEN + leaderB.getName() +
					"'s party" +
					CC.PRIMARY + " vs. "
					+ CC.RED + leaderA.getName() + "'s party";
		}

		partyA.broadcast(partyAFoundMatchMessage);
		partyB.broadcast(partyBFoundMatchMessage);

		List<UUID> playersA = new ArrayList<>(partyA.getMembers());
		List<UUID> playersB = new ArrayList<>(partyB.getMembers());

		MatchTeam teamA = new MatchTeam(leaderA.getUniqueId(), playersA, 0);
		MatchTeam teamB = new MatchTeam(leaderB.getUniqueId(), playersB, 1);

		Match match = new Match(arena, kit, type, teamA, teamB);

		this.plugin.getMatchManager().createMatch(match);

		this.queued.remove(partyA.getLeader());
		this.queued.remove(partyB.getLeader());
	}

	public void removePartyFromQueue(Party party) {
		QueueEntry entry = this.queued.get(party.getLeader());

		this.queued.remove(party.getLeader());

		party.members().forEach(PracticeData::sendToSpawnAndReset);

		String type = entry.getQueueType().isRanked() ? "Ranked" : "Unranked";

		party.broadcast(
				CC.PRIMARY + "Your party was removed from the " + CC.SECONDARY + type + " " + entry.getKitName() +
						CC.PRIMARY + " queue.");
	}
}
