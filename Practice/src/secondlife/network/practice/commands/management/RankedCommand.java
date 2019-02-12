package secondlife.network.practice.commands.management;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.utilties.PlayerUtil;
import secondlife.network.vituz.utilties.Permission;

public class RankedCommand extends Command {

	private final Practice plugin = Practice.getInstance();

	public RankedCommand() {
		super("ranked");

		setDescription("Manage server ranked mode.");
		setUsage(CC.RED + "Usage: /ranked");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player) || !PlayerUtil.testPermission(sender, Permission.OP_PERMISSION)) return true;

		boolean enabled = this.plugin.getQueueManager().isRankedEnabled();

		this.plugin.getQueueManager().setRankedEnabled(!enabled);
		sender.sendMessage(CC.GREEN + "Ranked matches are now " + (!enabled ? CC.GREEN + "enabled" : CC.RED + "disabled") + CC.GREEN + ".");
		return true;
	}
}
