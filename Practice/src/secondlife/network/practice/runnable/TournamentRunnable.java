package secondlife.network.practice.runnable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.practice.Practice;
import secondlife.network.practice.kit.Kit;
import secondlife.network.practice.match.Match;
import secondlife.network.practice.match.MatchTeam;
import secondlife.network.practice.party.Party;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.queue.QueueType;
import secondlife.network.practice.tournament.Tournament;
import secondlife.network.practice.tournament.TournamentState;
import secondlife.network.practice.tournament.TournamentTeam;
import secondlife.network.vituz.utilties.Color;

import java.util.*;

@RequiredArgsConstructor
public class TournamentRunnable extends BukkitRunnable {

	private final Practice plugin = Practice.getInstance();
	private final Tournament tournament;

	@Override
	public void run() {
		if (this.tournament.getTournamentState() == TournamentState.STARTING) {
			int countdown = this.tournament.decrementCountdown();
			if (countdown == 0) {
				if (this.tournament.getCurrentRound() == 1) {
					Set<UUID> players = Sets.newConcurrentHashSet(this.tournament.getPlayers());

					//Making Teams
					for (UUID player : players) {
						Party party = this.plugin.getPartyManager().getParty(player);

						if (party != null) {
							TournamentTeam team = new TournamentTeam(party.getLeader(), Lists.newArrayList(party.getMembers()));
							this.tournament.addAliveTeam(team);
							for (UUID member : party.getMembers()) {
								players.remove(member);
								tournament.setPlayerTeam(member, team);
							}
						}
					}

					List<UUID> currentTeam = null;

					for (UUID player : players) {
						if (currentTeam == null) {
							currentTeam = new ArrayList<>();
						}

						currentTeam.add(player);

						if (currentTeam.size() == this.tournament.getTeamSize()) {
							TournamentTeam team = new TournamentTeam(currentTeam.get(0), currentTeam);
							this.tournament.addAliveTeam(team);
							for (UUID teammate : team.getPlayers()) {
								tournament.setPlayerTeam(teammate, team);
							}
							currentTeam = null;
						}
					}
				}

				List<TournamentTeam> teams = this.tournament.getAliveTeams();

				Collections.shuffle(teams);

				for (int i = 0; i < teams.size(); i += 2) {
					TournamentTeam teamA = teams.get(i);

					if (teams.size() > i + 1) {
						TournamentTeam teamB = teams.get(i + 1);

						for (UUID playerUUID : teamA.getAlivePlayers()) {
							this.removeSpectator(playerUUID);
						}
						for (UUID playerUUID : teamB.getAlivePlayers()) {
							this.removeSpectator(playerUUID);
						}

						MatchTeam matchTeamA = new MatchTeam(teamA.getLeader(), new ArrayList<>(teamA.getAlivePlayers()), 0);
						MatchTeam matchTeamB = new MatchTeam(teamB.getLeader(), new ArrayList<>(teamB.getAlivePlayers()), 1);

						Kit kit = this.plugin.getKitManager().getKit(this.tournament.getKitName());

						Match match = new Match
								(this.plugin.getArenaManager().getRandomArena(kit), kit, QueueType.UNRANKED, matchTeamA, matchTeamB);

						Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
						Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());

						match.broadcast("&eStarting a match with kit &d" + kit.getName() + " &ebetween &d" + leaderA.getName() + " &eand &d" + leaderB.getName() + "&e.");

						this.plugin.getMatchManager().createMatch(match);

						this.tournament.addMatch(match.getMatchId());

						this.plugin.getTournamentManager().addTournamentMatch(match.getMatchId(), tournament.getId());
					} else {
						for (UUID playerUUID : teamA.getAlivePlayers()) {
							Player player = this.plugin.getServer().getPlayer(playerUUID);

							player.sendMessage(Color.translate("&eYou will be byed this round."));
						}
					}
				}

				StringBuilder builder = new StringBuilder();

				builder.append(ChatColor.GREEN).append("Round ").append(this.tournament.getCurrentRound()).append(ChatColor.YELLOW).append(" has started!\n");
				builder.append(ChatColor.YELLOW).append("Tip: Use ").append(ChatColor.GREEN).append("/tournament status ").append(this.tournament.getId()).append(ChatColor.YELLOW)
						.append(" to see who's fighting + the status of the tournament!");

				this.tournament.broadcastWithSound(builder.toString(), Sound.FIREWORK_BLAST);

				this.tournament.setTournamentState(TournamentState.FIGHTING);
			} else if ((countdown % 5 == 0 || countdown < 5) && countdown > 0) {
				this.tournament.broadcastWithSound("&dRound " + this.tournament.getCurrentRound() +" &eis starting in &d" + countdown + " &eseconds!", Sound.CLICK);
			}
		}
	}

	private void removeSpectator(UUID playerUUID) {
		Player player = this.plugin.getServer().getPlayer(playerUUID);

		if (player != null) {
			PracticeData playerData = PracticeData.getByName(player.getName());

			if (playerData.getPlayerState() == PlayerState.SPECTATING) {
				this.plugin.getMatchManager().removeSpectator(player);
			}
		}
	}
}
