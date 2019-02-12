package secondlife.network.practice.commands.event;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.events.EventState;
import secondlife.network.practice.events.PracticeEvent;

public class HostCommand extends Command {

	private final Practice plugin = Practice.getInstance();

	public HostCommand() {
		super("host");

		setDescription("Host an event.");
		setUsage(ChatColor.RED + "Usage: /host <event>");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		if(!player.hasPermission("secondlife.host")) {
			player.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}

		if(args.length < 1) {
			player.sendMessage(usageMessage);
			return true;
		}

		String eventName = args[0];

		if(eventName == null) return true;

		if(plugin.getEventManager().getByName(eventName) == null) {
			player.sendMessage(ChatColor.RED + "That event doesn't exist.");
			player.sendMessage(ChatColor.RED + "Available events: Sumo, OITC, Parkour");
			return true;
		}

		if(eventName.toUpperCase().equalsIgnoreCase("REDROVER")) {
			player.sendMessage(ChatColor.RED + "This event is currently disabled.");
			return true;
		}

		if(System.currentTimeMillis() < plugin.getEventManager().getCooldown()) {
			player.sendMessage(ChatColor.RED + "There is a cooldown. Event can't start at this moment.");
			return true;
		}

		PracticeEvent event = plugin.getEventManager().getByName(eventName);
		if(event.getState() != EventState.UNANNOUNCED) {
			player.sendMessage(ChatColor.RED + "There is currently an active event.");
			return true;
		}

		boolean eventBeingHosted = plugin.getEventManager().getEvents().values().stream().anyMatch(e -> e.getState() != EventState.UNANNOUNCED);
		if(eventBeingHosted) {
			player.sendMessage(ChatColor.RED + "There is currently an active event.");
			return true;
		}

		event.setLimit(50);

		if(args.length == 2 && player.hasPermission("secondlife.op")) {
			if(!NumberUtils.isNumber(args[1])) {
				player.sendMessage(ChatColor.RED + "That's not a correct amount.");
				return true;
			}

			event.setLimit(Integer.parseInt(args[1]));
		}

		Practice.getInstance().getEventManager().hostEvent(event, player);
		return true;
	}
}
