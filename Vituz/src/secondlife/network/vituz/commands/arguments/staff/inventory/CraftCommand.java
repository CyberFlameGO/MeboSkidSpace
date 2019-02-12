package secondlife.network.vituz.commands.arguments.staff.inventory;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class CraftCommand extends BaseCommand {

	public CraftCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "craft";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(VituzAPI.getServerName().equals("Factions")) {
			if(!player.hasPermission("secondlife.craft")) {
				player.sendMessage(Msg.NO_PERMISSION);
				return;
			}

			player.openWorkbench(player.getLocation(), true);

			player.sendMessage(Color.translate("&eYou have opened workbench."));
		} else {
			if(!player.hasPermission("secondlife.staff")) {
				player.sendMessage(Msg.NO_PERMISSION);
				return;
			}

			player.openWorkbench(player.getLocation(), true);

			player.sendMessage(Color.translate("&eYou have opened workbench."));
		}
	}
}
