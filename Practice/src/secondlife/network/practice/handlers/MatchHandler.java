package secondlife.network.practice.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import secondlife.network.practice.Practice;
import secondlife.network.practice.inventory.InventorySnapshot;
import secondlife.network.practice.kit.Kit;
import secondlife.network.practice.match.Match;
import secondlife.network.practice.match.MatchState;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.runnable.MatchRunnable;
import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.utilties.CustomLocation;
import secondlife.network.practice.utilties.EloUtil;
import secondlife.network.practice.utilties.PlayerUtil;
import secondlife.network.practice.utilties.event.match.MatchEndEvent;
import secondlife.network.practice.utilties.event.match.MatchStartEvent;
import secondlife.network.vituz.utilties.ActionMessage;

import java.util.*;

public class MatchHandler implements Listener {

	private final Practice plugin = Practice.getInstance();

	@EventHandler
	public void onMatchStart(MatchStartEvent event) {
		Match match = event.getMatch();
		Kit kit = match.getKit();

		if (!kit.isEnabled()) {
			match.broadcast(CC.RED + "This kit is currently disabled, try another kit.");
			this.plugin.getMatchManager().removeMatch(match);
			return;
		}

		if (kit.isBuild() || kit.isSpleef() || kit.isBedWars()) {
			if (match.getArena().getAvailableArenas().size() > 0) {
				match.setStandaloneArena(match.getArena().getAvailableArena());
				this.plugin.getArenaManager().setArenaMatchUUID(match.getStandaloneArena(), match.getMatchId());
			} else {
				match.broadcast(CC.RED + "There are no arenas available.");
				this.plugin.getMatchManager().removeMatch(match);
				return;
			}
		}

		Set<Player> matchPlayers = new HashSet<>();

		match.getTeams().forEach(team -> team.alivePlayers().forEach(player -> {
			matchPlayers.add(player);

			this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());

			PracticeData playerData = PracticeData.getByName(player.getName());

			player.setAllowFlight(false);
			player.setFlying(false);

			playerData.setCurrentMatchID(match.getMatchId());
			playerData.setTeamID(team.getTeamID());

			playerData.setMissedPots(0);
			playerData.setLongestCombo(0);
			playerData.setCombo(0);
			playerData.setHits(0);

			PlayerUtil.clearPlayer(player);

			CustomLocation locationA = match.getStandaloneArena() != null ? match.getStandaloneArena().getA() : match.getArena().getA();
			CustomLocation locationB = match.getStandaloneArena() != null ? match.getStandaloneArena().getB() : match.getArena().getB();
			player.teleport(team.getTeamID() == 1 ? locationA.toBukkitLocation() : locationB.toBukkitLocation());

			if(kit.isBedWars()) {
				Block a = match.getArena().getABed();
				Block b = match.getArena().getBBed();

				playerData.setBed(team.getTeamID() == 1 ? a : b);
			}

			player.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "WARNING " + ChatColor.RED + "Butterfly Clicking can result in a ban.");

			if(kit.isBuild()) {
				player.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "WARNING " + ChatColor.RED + "Stacking Blocks will result in a ban.");
			}

			if (kit.isCombo()) {
				player.setMaximumNoDamageTicks(3);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 * 8, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60 * 8, 1));
			}

			if(kit.getName().equals("Gapple")) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 * 8, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60 * 8, 1));
			}

			if (!match.isRedrover()) {
				this.plugin.getMatchManager().giveKits(player, kit);

				playerData.setPlayerState(PlayerState.FIGHTING);
			} else {
				this.plugin.getMatchManager().addRedroverSpectator(player, match);
			}
		}));

		for (Player player : matchPlayers) {
			for (Player online : this.plugin.getServer().getOnlinePlayers()) {
				online.hidePlayer(player);
				player.hidePlayer(online);
			}
		}

		for (Player player : matchPlayers) {
			for (Player other : matchPlayers) {
				player.showPlayer(other);
			}
		}

		new MatchRunnable(match).runTaskTimer(this.plugin, 20L, 20L);
	}

	@EventHandler
	public void onMatchEnd(MatchEndEvent event) {
		Match match = event.getMatch();
		ActionMessage winnerClickable = new ActionMessage();
		ActionMessage loserClickable = new ActionMessage();

		winnerClickable.addText("&aWinner(s): ");
		loserClickable.addText("&cLoser(s): ");

		match.broadcast(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
		match.broadcast(ChatColor.YELLOW + "Match Results: " + ChatColor.GRAY + "(Clickable Inventories)");

		match.setMatchState(MatchState.ENDING);
		match.setWinningTeamId(event.getWinningTeam().getTeamID());
		match.setCountdown(4);

		if (match.isFFA()) {
			Player winner = this.plugin.getServer().getPlayer(event.getWinningTeam().getAlivePlayers().get(0));

			event.getWinningTeam().players().forEach(player -> {
				if (!match.hasSnapshot(player.getUniqueId())) {
					match.addSnapshot(player);
				}

				if (player.getUniqueId().equals(winner.getUniqueId())) {
					winnerClickable.addText(ChatColor.GREEN + player.getName() + " ").addHoverText(ChatColor.GRAY + "Click to view inventory").setClickEvent(ActionMessage.ClickableType.RunCommand,  "/inv " + match.getSnapshot(player.getUniqueId()).getSnapshotId());
				}
				else {
					loserClickable.addText(ChatColor.RED + player.getName() + " ").addHoverText(ChatColor.GRAY + "Click to view inventory").setClickEvent(ActionMessage.ClickableType.RunCommand, "/inv " + match.getSnapshot(player.getUniqueId()).getSnapshotId());
				}

			});

			for (InventorySnapshot snapshot : match.getSnapshots().values()) {
				this.plugin.getInventoryManager().addSnapshot(snapshot);
			}

			match.broadcast(winnerClickable);
			match.broadcast(loserClickable);
			match.broadcast(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
		}
		else if (match.isRedrover()) {
			match.broadcast(ChatColor.GREEN + event.getWinningTeam().getLeaderName() + ChatColor.YELLOW + " has won the redrover!");
		}
		else {
			Map<UUID, InventorySnapshot> inventorySnapshotMap = new LinkedHashMap<>();

			match.getTeams().forEach(team -> team.players().forEach(player -> {
				if (!match.hasSnapshot(player.getUniqueId())) {
					match.addSnapshot(player);
				}

				inventorySnapshotMap.put(player.getUniqueId(), match.getSnapshot(player.getUniqueId()));

				boolean onWinningTeam = PracticeData.getByKurac(player.getUniqueId()).getTeamID() == event.getWinningTeam().getTeamID();

				if (onWinningTeam) {
					winnerClickable.addText(ChatColor.GREEN + player.getName() + " ").addHoverText(ChatColor.GRAY + "Click to view inventory").setClickEvent(ActionMessage.ClickableType.RunCommand, "/inv " + match.getSnapshot(player.getUniqueId()).getSnapshotId());
				}
				else {
					loserClickable.addText(ChatColor.RED + player.getName() + " ").addHoverText(ChatColor.GRAY + "Click to view inventory").setClickEvent(ActionMessage.ClickableType.RunCommand, "/inv " + match.getSnapshot(player.getUniqueId()).getSnapshotId());
				}

				player.setMaximumNoDamageTicks(20); // Double setting the damage ticks.
			}));

			for (InventorySnapshot snapshot : match.getSnapshots().values()) {
				this.plugin.getInventoryManager().addSnapshot(snapshot);
			}

			match.broadcast(winnerClickable);
			match.broadcast(loserClickable);

			if (match.getType().isRanked()) {
				String kitName = match.getKit().getName();

				Player winnerLeader = this.plugin.getServer().getPlayer(event.getWinningTeam().getPlayers().get(0));
				PracticeData winnerLeaderData = PracticeData.getByKurac(winnerLeader.getUniqueId());
				Player loserLeader = this.plugin.getServer().getPlayer(event.getLosingTeam().getPlayers().get(0));
				PracticeData loserLeaderData = PracticeData.getByKurac(loserLeader.getUniqueId());

				String eloMessage;

				int[] preElo = new int[2];
				int[] newElo = new int[2];
				int winnerElo = 0;
				int loserElo = 0;
				int newWinnerElo = 0;
				int newLoserElo = 0;

				if (event.getWinningTeam().getPlayers().size() == 2) {

					UUID winnerUUID = Bukkit.getPlayer(event.getWinningTeam().getLeader()) == null ? event.getWinningTeam().getPlayers().get(0) : event.getWinningTeam().getLeader();
					Player winnerMember = this.plugin.getServer().getPlayer(winnerUUID);
					PracticeData winnerMemberData = PracticeData.getByKurac(winnerMember.getUniqueId());

					UUID loserUUID = Bukkit.getPlayer(event.getLosingTeam().getLeader()) == null ? event.getLosingTeam().getPlayers().get(0) : event.getLosingTeam().getLeader();
					Player loserMember = this.plugin.getServer().getPlayer(loserUUID);
					PracticeData loserMemberData = PracticeData.getByKurac(loserMember.getUniqueId());

					winnerElo = winnerMemberData.getPartyElo(kitName);
					loserElo = loserMemberData.getPartyElo(kitName);

					preElo[0] = winnerElo;
					preElo[1] = loserElo;

					newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true);
					newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false);

					newElo[0] = newWinnerElo;
					newElo[1] = newLoserElo;

					winnerMemberData.setPartyElo(kitName, newWinnerElo);
					loserMemberData.setPartyElo(kitName, newLoserElo);

					eloMessage = ChatColor.YELLOW + "Elo Changes: " + ChatColor.GREEN + winnerLeader.getName() + ", " + winnerMember.getName() + " +" + (newWinnerElo - winnerElo) + " (" + newWinnerElo + ") " + ChatColor.RED + loserLeader.getName() + "," + " " + loserMember.getName() + " " + " " + (newLoserElo - loserElo) + " (" + newLoserElo + ")";
				}
				else {
					winnerElo = winnerLeaderData.getElo(kitName);
					loserElo = loserLeaderData.getElo(kitName);

					preElo[0] = winnerElo;
					preElo[1] = loserElo;

					newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true);
					newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false);

					newElo[0] = newWinnerElo;
					newElo[1] = newLoserElo;

					eloMessage = ChatColor.YELLOW + "Elo Changes: " + ChatColor.GREEN + winnerLeader.getName() + " +" + (newWinnerElo - winnerElo) + " (" + newWinnerElo + ") " + ChatColor.RED + loserLeader.getName() + " " + (newLoserElo - loserElo) + " (" + newLoserElo + ")";

					winnerLeaderData.setElo(kitName, newWinnerElo);
					loserLeaderData.setElo(kitName, newLoserElo);

					winnerLeaderData.setWins(kitName, winnerLeaderData.getWins(kitName) + 1);
					loserLeaderData.setLosses(kitName, loserLeaderData.getLosses(kitName) + 1);
				}

				match.broadcast(eloMessage);
			}

			match.broadcast(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");

			this.plugin.getMatchManager().saveRematches(match);
		}
	}
}
