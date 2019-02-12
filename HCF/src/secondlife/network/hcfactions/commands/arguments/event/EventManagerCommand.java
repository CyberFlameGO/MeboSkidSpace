package secondlife.network.hcfactions.commands.arguments.event;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.events.EventState;
import secondlife.network.hcfactions.events.KitMapEvent;
import secondlife.network.vituz.utilties.Permission;

public class EventManagerCommand extends Command {

	private final HCF plugin = HCF.getInstance();

	public EventManagerCommand() {
		super("eventmanager");

		setDescription("Manage an event.");
		setUsage(ChatColor.RED + "Usage: /eventmanager <start/end/status/cooldown> <event>");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		if(!player.hasPermission(Permission.OP_PERMISSION)) {
			player.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}

		if(args.length < 2) {
			player.sendMessage(usageMessage);
			return true;
		}

		String action = args[0];
		String eventName = args[1];

		if(plugin.getEventManager().getByName(eventName) == null) {
			player.sendMessage(ChatColor.RED + "That event doesn't exist.");
			return true;
		}

		KitMapEvent event = plugin.getEventManager().getByName(eventName);

		if(action.toUpperCase().equalsIgnoreCase("START") && event.getState() == EventState.WAITING) {
			event.getCountdownTask().setTimeUntilStart(5);
			player.sendMessage(ChatColor.RED + "Event was force started.");
		} else if(action.toUpperCase().equalsIgnoreCase("END")  && event.getState() == EventState.STARTED) {
			event.end();
			player.sendMessage(ChatColor.RED + "Event was cancelled.");
		} else if(action.toUpperCase().equalsIgnoreCase("STATUS")) {
			String[] message = new String[] {
					ChatColor.YELLOW + "Event: " + ChatColor.WHITE + event.getName(),
					ChatColor.YELLOW + "Host: " + ChatColor.WHITE + (event.getHost() == null ? "Player Left" : event.getHost().getName()),
					ChatColor.YELLOW + "Players: " + ChatColor.WHITE + event.getPlayers().size() + "/" + event.getLimit(),
					ChatColor.YELLOW + "State: " + ChatColor.WHITE + event.getState().name(),
			};

			player.sendMessage(message);
		} else if(action.toUpperCase().equalsIgnoreCase("COOLDOWN")) {
			this.plugin.getEventManager().setCooldown(0L);

			player.sendMessage(ChatColor.RED + "Event cooldown was cancelled.");
		} else {
			player.sendMessage(this.usageMessage);
		}

		return true;
	}
}
