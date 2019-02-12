package secondlife.network.practice.commands.duel;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.kit.Kit;
import secondlife.network.practice.managers.PartyManager;
import secondlife.network.practice.match.Match;
import secondlife.network.practice.match.MatchRequest;
import secondlife.network.practice.match.MatchTeam;
import secondlife.network.practice.party.Party;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.queue.QueueType;
import secondlife.network.practice.utilties.CC;
import secondlife.network.vituz.utilties.Msg;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AcceptCommand extends Command {
	private final Practice plugin = Practice.getInstance();

	public AcceptCommand() {
		super("accept");

		setDescription("Accept a player's duel.");
		setUsage(CC.RED + "Usage: /accept <player>");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		if(args.length < 1) {
			player.sendMessage(usageMessage);
			return true;
		}

		PracticeData playerData = PracticeData.getByName(player.getName());

		if(playerData.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(CC.RED + "You can't do this in your current state.");
			return true;
		}

		Player target = this.plugin.getServer().getPlayer(args[0]);

		if(Msg.checkOffline(sender, args[0])) return true;

		if(player.getName().equals(target.getName())) {
			player.sendMessage(CC.RED + "You can't duel yourself.");
			return true;
		}

		PracticeData targetData = PracticeData.getByName(target.getName());

		if(targetData.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(CC.RED + "Player isn't in spawn.");
			return true;
		}

		MatchRequest request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId());

		if(args.length > 1) {
			Kit kit = this.plugin.getKitManager().getKit(args[1]);

			if(kit != null) {
				request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId(), kit.getName());
			}
		}

		if(request == null) {
			player.sendMessage(CC.RED + "You don't have a match request from that player.");
			return true;
		}

		if(request.getRequester().equals(target.getUniqueId())) {
			List<UUID> playersA = new ArrayList<>();
			List<UUID> playersB = new ArrayList<>();

			PartyManager partyManager = this.plugin.getPartyManager();

			Party party = partyManager.getParty(player.getUniqueId());
			Party targetParty = partyManager.getParty(target.getUniqueId());

			if(request.isParty()) {
				if(party != null && targetParty != null && partyManager.isLeader(target.getUniqueId()) && partyManager.isLeader(target.getUniqueId())) {
					playersA.addAll(party.getMembers());
					playersB.addAll(targetParty.getMembers());
				} else {
					player.sendMessage(CC.RED + "Either you or that player isn't a party leader.");
					return true;
				}
			} else {
				if(party == null && targetParty == null) {
					playersA.add(player.getUniqueId());
					playersB.add(target.getUniqueId());
				} else {
					player.sendMessage(CC.RED + "One of you are in a party.");
					return true;
				}
			}

			Kit kit = this.plugin.getKitManager().getKit(request.getKitName());

			MatchTeam teamA = new MatchTeam(target.getUniqueId(), playersB, 0);
			MatchTeam teamB = new MatchTeam(player.getUniqueId(), playersA, 1);

			Match match = new Match(request.getArena(), kit, QueueType.UNRANKED, teamA, teamB);

			Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
			Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());

			match.broadcast(CC.PRIMARY + "Starting a match with kit " + CC.SECONDARY + request.getKitName() + CC.PRIMARY + " between " + CC.SECONDARY + leaderA.getName() + CC.PRIMARY + " and " + CC.SECONDARY + leaderB.getName() + CC.PRIMARY + ".");

			this.plugin.getMatchManager().createMatch(match);
		}

		return true;
	}
}
