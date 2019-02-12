package secondlife.network.practice.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.CC;

import java.util.Arrays;

public class ToggleSpectatorsCommand extends Command {
	private final Practice plugin = Practice.getInstance();

	public ToggleSpectatorsCommand() {
		super("tsp");

		setDescription("Toggles a player's ability to spectate you on or off.");
		setUsage(CC.RED + "Usage: /tsp");
		setAliases(Arrays.asList("togglesp", "togglespec", "togglespectator", "togglespectators"));
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player)) return true;

		Player player = (Player) sender;
		PracticeData playerData = PracticeData.getByName(player.getName());

		playerData.setAllowingSpectators(!playerData.isAllowingSpectators());
		player.sendMessage(playerData.isAllowingSpectators() ? CC.GREEN + "You are now allowing spectators." : CC.RED + "You are no longer allowing spectators.");
		return true;
	}
}
