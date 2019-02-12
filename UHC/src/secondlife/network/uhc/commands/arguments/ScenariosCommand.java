package secondlife.network.uhc.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.uhc.UHC;
import secondlife.network.uhc.commands.BaseCommand;
import secondlife.network.uhc.managers.InventoryManager;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class ScenariosCommand extends BaseCommand {

	public ScenariosCommand(UHC plugin) {
		super(plugin);
		
		this.command = "scenarios";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			InventoryManager.setScenarioInfo(player);
		} else {
			if(args[0].equalsIgnoreCase("listall") || args[0].equalsIgnoreCase("all")) {
				if(!player.hasPermission(Permission.OP_PERMISSION)) {
					player.sendMessage(Msg.NO_PERMISSION);
					return;
				}
				player.openInventory(InventoryManager.toggleScenarios);
			}
		}
	}
}
