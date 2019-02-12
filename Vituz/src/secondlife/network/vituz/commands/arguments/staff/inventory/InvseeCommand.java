package secondlife.network.vituz.commands.arguments.staff.inventory;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class InvseeCommand extends BaseCommand {

	public InvseeCommand(Vituz plugin) {
		super(plugin);

		this.command = "invsee";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			player.sendMessage(Color.translate("&cUsage: /invsee <player>"));
		} else {
			Player target = Bukkit.getPlayer(args[0]);
			
			if(Msg.checkOffline(player, args[0])) return;

			plugin.getEssentialsManager().getInventory(player, target);

			player.sendMessage(Color.translate("&eYou have opened inventory of " + target.getDisplayName()));
		}
	}

}
