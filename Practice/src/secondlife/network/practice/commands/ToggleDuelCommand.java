package secondlife.network.practice.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.CC;

import java.util.Arrays;

public class ToggleDuelCommand extends Command {
	private final Practice plugin = Practice.getInstance();

	public ToggleDuelCommand() {
		super("tdr");

		setDescription("Toggles a player's duel requests on or off.");
		setUsage(CC.RED + "Usage: /tdr");
		setAliases(Arrays.asList("toggleduel", "toggleduels", "td"));
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player)) return true;

		Player player = (Player) sender;
		PracticeData playerData = PracticeData.getByName(player.getName());

		playerData.setAcceptingDuels(!playerData.isAcceptingDuels());
		player.sendMessage(playerData.isAcceptingDuels() ? CC.GREEN + "You are now accepting duel requests." : CC.RED + "You are no longer accepting duel requests.");
		return true;
	}
}
