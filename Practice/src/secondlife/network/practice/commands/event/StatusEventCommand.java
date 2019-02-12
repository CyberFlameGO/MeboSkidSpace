package secondlife.network.practice.commands.event;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.match.Match;
import secondlife.network.practice.match.MatchTeam;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.tournament.Tournament;
import secondlife.network.practice.utilties.CC;
import secondlife.network.vituz.utilties.ActionMessage;

import java.util.UUID;

public class StatusEventCommand extends Command {

	private final Practice plugin = Practice.getInstance();

	public StatusEventCommand() {
		super("status");

		setDescription("Show an event or tournament status.");
		setUsage(ChatColor.RED + "Usage: /status");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		PracticeData playerData = PracticeData.getByName(player.getName());

			if(playerData.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(CC.RED + "You can't do this in your current state.");
			return true;
		}

		if(this.plugin.getTournamentManager().getTournaments().size() == 0) {
			player.sendMessage(ChatColor.RED + "There is no available tournaments.");
			return true;
		}

		for(Tournament tournament : this.plugin.getTournamentManager().getTournaments().values()) {
			if(tournament == null) {
				player.sendMessage(ChatColor.RED + "This tournament doesn't exist.");
				return true;
			}


			player.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
			player.sendMessage(" ");
			player.sendMessage(ChatColor.YELLOW.toString() + "Tournament (" + tournament.getTeamSize() + "v" + tournament.getTeamSize() + ") " + ChatColor.GOLD.toString() + tournament.getKitName());

			if(tournament.getMatches().size() == 0) {
				player.sendMessage(ChatColor.RED + "There is no available matches.");
				player.sendMessage(" ");
				player.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
				return true;
			}

			for(UUID matchUUID : tournament.getMatches()) {
				Match match = this.plugin.getMatchManager().getMatchFromUUID(matchUUID);

				MatchTeam teamA = match.getTeams().get(0);
				MatchTeam teamB = match.getTeams().get(1);

				String teamANames = (tournament.getTeamSize() > 1 ? teamA.getLeaderName() + "'s Party" : teamA.getLeaderName());
				String teamBNames = (tournament.getTeamSize() > 1 ? teamB.getLeaderName() + "'s Party" : teamB.getLeaderName());

				ActionMessage actionMessage = new ActionMessage();
				actionMessage.addText(ChatColor.WHITE.toString() + ChatColor.BOLD + "* " + ChatColor.GOLD.toString() + teamANames + " vs " + teamBNames + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "[Click to Spectate]").addHoverText(ChatColor.GRAY + "Click to spectate").setClickEvent(ActionMessage.ClickableType.RunCommand, "/spectate " + teamA.getLeaderName());

				actionMessage.sendToPlayer(player);
			}

			player.sendMessage(" ");
			player.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
		}

		return true;
	}
}
