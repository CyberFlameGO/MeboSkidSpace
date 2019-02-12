package secondlife.network.practice.commands.duel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.events.PracticeEvent;
import secondlife.network.practice.events.oitc.OITCEvent;
import secondlife.network.practice.events.parkour.ParkourEvent;
import secondlife.network.practice.events.sumo.SumoEvent;
import secondlife.network.practice.match.Match;
import secondlife.network.practice.match.MatchTeam;
import secondlife.network.practice.party.Party;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.CC;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.utilties.Msg;

import java.util.Arrays;

public class SpectateCommand extends Command {
	private final Practice plugin = Practice.getInstance();

	public SpectateCommand() {
		super("spec");

		setDescription("Spectate a player's match.");
		setUsage(CC.RED + "Usage: /spectate <player>");
		setAliases(Arrays.asList("sp", "spect", "spectate"));
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
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

		if(party != null || (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.SPECTATING)) {
			player.sendMessage(CC.RED + "You can't do this in your current state.");
			return true;
		}

		Player target = this.plugin.getServer().getPlayer(args[0]);

		if(Msg.checkOffline(sender, args[0])) return true;

		PracticeData targetData = PracticeData.getByName(target.getName());

		if(targetData.getPlayerState() == PlayerState.EVENT) {
			PracticeEvent event = this.plugin.getEventManager().getEventPlaying(target);

			if(event == null) {
				player.sendMessage(ChatColor.RED + "That player is currently not in an event.");
				return true;
			}

			if(event instanceof SumoEvent) {
				player.performCommand("eventspectate Sumo");
			} else if(event instanceof OITCEvent) {
				player.performCommand("eventspectate OITC");
			} else if(event instanceof ParkourEvent) {
				player.performCommand("eventspectate Parkour");
			}

			return true;
		}

		if(targetData.getPlayerState() != PlayerState.FIGHTING) {
			player.sendMessage(CC.RED + "Player isn't in a match.");
			return true;
		}

		Match targetMatch = this.plugin.getMatchManager().getMatch(targetData);

		if(!targetMatch.isParty()) {
			if(!VituzAPI.getRankName(player.getName()).equals("TrialMod")) {
				if(!targetData.isAllowingSpectators()) {
					player.sendMessage(CC.RED + "This player isn't allowing spectators.");
					return true;
				}

				MatchTeam team = targetMatch.getTeams().get(0);
				MatchTeam team2 = targetMatch.getTeams().get(1);

				Player a = Bukkit.getPlayer(team.getPlayers().get(0));
				Player b = Bukkit.getPlayer(team2.getPlayers().get(0));

				PracticeData otherPlayerData = PracticeData.getByName(a.getName() == target.getName() ? b.getName() : a.getName());

				if(otherPlayerData != null && !otherPlayerData.isAllowingSpectators()) {
					player.sendMessage(CC.RED + "The player this player is dueling isn't allowing spectators.");
					return true;
				}
			}
		}

		if(playerData.getPlayerState() == PlayerState.SPECTATING) {
			Match match = this.plugin.getMatchManager().getSpectatingMatch(player.getUniqueId());

			if(match.equals(targetMatch)) {
				player.sendMessage(CC.RED + "You are already spectating this match.");
				return true;
			}

			match.removeSpectator(player.getUniqueId());
		}

		player.sendMessage(CC.PRIMARY + "You are now spectating " + CC.SECONDARY + target.getName() + CC.PRIMARY + ".");
		this.plugin.getMatchManager().addSpectator(player, playerData, target, targetMatch);
		return true;
	}
}
