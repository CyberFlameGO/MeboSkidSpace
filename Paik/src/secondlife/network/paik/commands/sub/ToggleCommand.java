package secondlife.network.paik.commands.sub;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.commands.PaikCommand;
import secondlife.network.paik.utilties.command.Command;
import secondlife.network.paik.utilties.command.CommandArgs;

import java.util.HashSet;
import java.util.Set;

public class ToggleCommand extends PaikCommand {

	public static Set<String> DISABLED_CHECKS = new HashSet<>();

	@Command(name = "togglecheck", aliases = { "check" } ,permission = "secondlife.op")
	public void onCommand(CommandArgs command) {
		Player player = command.getPlayer();
		String[] args = command.getArgs();

		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Usage: /togglecheck <check>");
			return;
		}

		String check = args[0].toUpperCase();

		if (!ToggleCommand.DISABLED_CHECKS.remove(check)) {
			ToggleCommand.DISABLED_CHECKS.add(check);
			player.sendMessage(ChatColor.RED + check + " has been disabled.");
		} else {
			player.sendMessage(ChatColor.GREEN + check + " has been enabled.");
		}
	}
}
