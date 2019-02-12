package secondlife.network.practice.commands.event;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.events.EventState;
import secondlife.network.practice.events.PracticeEvent;
import secondlife.network.practice.events.oitc.OITCEvent;
import secondlife.network.practice.events.parkour.ParkourEvent;
import secondlife.network.practice.events.sumo.SumoEvent;
import secondlife.network.practice.party.Party;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.CC;

import java.util.Arrays;

public class SpectateEventCommand extends Command {

	private final Practice plugin = Practice.getInstance();

	public SpectateEventCommand() {
		super("eventspectate");

		setDescription("Spectate an event.");
		setUsage(ChatColor.RED + "Usage: /eventspectate <event>");
		setAliases(Arrays.asList("eventspec", "specevent"));
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player)) return true;


		Player player = (Player) sender;

		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return true;
		}

		PracticeData playerData = PracticeData.getByName(player.getName());
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

		if (party != null || (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.SPECTATING)) {
			player.sendMessage(CC.RED + "You can't do this in your current state.");
			return true;
		}

		PracticeEvent event = this.plugin.getEventManager().getByName(args[0]);

		if(event == null) {
			player.sendMessage(ChatColor.RED + "That player is currently not in an event.");
			return true;
		}

		if(event.getState() != EventState.STARTED) {
			player.sendMessage(ChatColor.RED + "That event hasn't started, please wait.");
			return true;
		}

		if (playerData.getPlayerState() == PlayerState.SPECTATING) {

			if(this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You are already spectating this event.");
				return true;
			}

			this.plugin.getEventManager().removeSpectator(player);
		}

		player.sendMessage(CC.PRIMARY + "You are now spectating " + CC.SECONDARY + event.getName() + " Event" + CC.PRIMARY + ".");

		if(event instanceof SumoEvent) {
			this.plugin.getEventManager().addSpectatorSumo(player, playerData, (SumoEvent) event);
		} else if(event instanceof OITCEvent) {
			this.plugin.getEventManager().addSpectatorOITC(player, playerData, (OITCEvent) event);
		} else if(event instanceof ParkourEvent) {
			this.plugin.getEventManager().addSpectatorParkour(player, playerData, (ParkourEvent) event);
		}

		return true;
	}
}
