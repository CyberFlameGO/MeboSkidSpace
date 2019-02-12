package secondlife.network.hcfactions.commands.arguments.event;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.events.KitMapEvent;

public class LeaveEventCommand extends Command {

	private final HCF plugin = HCF.getInstance();

	public LeaveEventCommand() {
		super("leave");

		setDescription("Leave an event or tournament.");
		setUsage(ChatColor.RED + "Usage: /leave");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		boolean inEvent = this.plugin.getEventManager().getEventPlaying(player) != null;

		if(inEvent) {
			this.leaveEvent(player);
		} else {
			player.sendMessage(ChatColor.RED + "There isn'thing to leave.");
		}

		return true;
	}

	private void leaveEvent(Player player) {
		KitMapEvent event = plugin.getEventManager().getEventPlaying(player);

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
