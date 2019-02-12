package secondlife.network.practice.commands.event;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.events.PracticeEvent;
import secondlife.network.practice.tournament.Tournament;

public class LeaveEventCommand extends Command {

	private final Practice plugin = Practice.getInstance();

	public LeaveEventCommand() {
		super("leave");

		setDescription("Leave an event or tournament.");
		setUsage(ChatColor.RED + "Usage: /leave");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		boolean inTournament = this.plugin.getTournamentManager().isInTournament(player.getUniqueId());
		boolean inEvent = this.plugin.getEventManager().getEventPlaying(player) != null;

		if(inEvent) {
			this.leaveEvent(player);
		} else if(inTournament) {
			this.leaveTournament(player);
		} else {
			player.sendMessage(ChatColor.RED + "There isn'thing to leave.");
		}

		return true;
	}

	private void leaveTournament(Player player) {
		Tournament tournament = this.plugin.getTournamentManager().getTournament(player.getUniqueId());

		if(tournament != null) {
			this.plugin.getTournamentManager().leaveTournament(player);
		}
	}

	private void leaveEvent(Player player) {
		PracticeEvent event = plugin.getEventManager().getEventPlaying(player);

		if(event == null) {
			player.sendMessage(ChatColor.RED + "That event doesn't exist.");
			return;
		}

		if(!this.plugin.getEventManager().isPlaying(player, event)) {
			player.sendMessage(ChatColor.RED + "You aren't in an event.");
			return;
		}

		event.leave(player);
	}
}
