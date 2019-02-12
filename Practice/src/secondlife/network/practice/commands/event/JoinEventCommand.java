package secondlife.network.practice.commands.event;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.events.EventState;
import secondlife.network.practice.events.PracticeEvent;
import secondlife.network.practice.party.Party;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.tournament.Tournament;
import secondlife.network.practice.tournament.TournamentState;
import secondlife.network.practice.utilties.CC;


public class JoinEventCommand extends Command {

	private final Practice plugin = Practice.getInstance();

	public JoinEventCommand() {
		super("join");

		setDescription("Join an event or tournament.");
		setUsage(ChatColor.RED + "Usage: /join <id>");
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

		if(this.plugin.getPartyManager().getParty(player.getUniqueId()) != null || (playerData.getPlayerState() != PlayerState.SPAWN)) {
			player.sendMessage(CC.RED + "You can't do this in your current state.");
			return true;
		}


		boolean inTournament = this.plugin.getTournamentManager().isInTournament(player.getUniqueId());
		boolean inEvent = this.plugin.getEventManager().getEventPlaying(player) != null;

		String eventId = args[0].toLowerCase();

		if(!NumberUtils.isNumber(eventId)) {
			PracticeEvent event = this.plugin.getEventManager().getByName(eventId);

			if(inTournament) {
				player.sendMessage(CC.RED + "You can't do this in your current state.");
				return true;
			}

			if(event == null) {
				player.sendMessage(ChatColor.RED + "That event doesn't exist.");
				return true;
			}

			if(event.getState() != EventState.WAITING) {
				player.sendMessage(ChatColor.RED + "That event is currently not available.");
				return true;
			}

			if(event.getPlayers().containsKey(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You are already in this event.");
				return true;
			}

			if(event.getPlayers().size() >= event.getLimit() && !player.hasPermission("secondlife.xenon")) {
				player.sendMessage(ChatColor.RED + "Sorry! The event is already full.");
			}

			event.join(player);
			return true;
		}


		if(inEvent) {
			player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
			return true;
		}

		if(this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
			player.sendMessage(ChatColor.RED + "You are currently in a tournament.");
			return true;
		}

		int id = Integer.parseInt(eventId);
		Tournament tournament = this.plugin.getTournamentManager().getTournament(id);

		if(tournament != null) {
			if(tournament.getTeamSize() > 1) {

				Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

				if(party != null && party.getMembers().size() != tournament.getTeamSize()) {
					player.sendMessage(ChatColor.RED + "The party size must be of " + tournament.getTeamSize() + " players.");
					return true;
				}
			}

			if(tournament.getSize() > tournament.getPlayers().size()) {
				if((tournament.getTournamentState() == TournamentState.WAITING || tournament.getTournamentState() == TournamentState.STARTING) && tournament.getCurrentRound() == 1) {
					this.plugin.getTournamentManager().joinTournament(id, player);
				} else {
					player.sendMessage(ChatColor.RED + "Sorry! The tournament already started.");
				}
			} else {
				player.sendMessage(ChatColor.RED + "Sorry! The tournament is already full.");
			}
		} else {
			player.sendMessage(ChatColor.RED + "That tournament doesn't exist.");
		}

		return true;
	}
}
