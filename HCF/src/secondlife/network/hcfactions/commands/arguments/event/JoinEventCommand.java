package secondlife.network.hcfactions.commands.arguments.event;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.events.EventState;
import secondlife.network.hcfactions.events.KitMapEvent;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.vituz.utilties.Color;


public class JoinEventCommand extends Command {

	private final HCF plugin = HCF.getInstance();

	public JoinEventCommand() {
		super("joinevent");

		setDescription("Join an event or tournament.");
		setUsage(ChatColor.RED + "Usage: /joinevent <id>");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		if(args.length < 1) {
			player.sendMessage(usageMessage);
			return true;
		}

		if(!RegisterHandler.getInstancee().getFactionManager().getFactionAt(player.getLocation()).getName().equals("Spawn")) {
			player.sendMessage(Color.translate("&cYou must be in safezone to join event!"));
			return false;
		}

		boolean inEvent = this.plugin.getEventManager().getEventPlaying(player) != null;

		String eventId = args[0].toLowerCase();

		if(!NumberUtils.isNumber(eventId)) {
			KitMapEvent event = this.plugin.getEventManager().getByName(eventId);

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

		return true;
	}
}
