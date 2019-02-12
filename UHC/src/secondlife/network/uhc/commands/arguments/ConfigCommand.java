package secondlife.network.uhc.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.uhc.UHC;
import secondlife.network.uhc.commands.BaseCommand;
import secondlife.network.uhc.managers.InventoryManager;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class ConfigCommand extends BaseCommand {

	public ConfigCommand(UHC plugin) {
		super(plugin);

		this.command = "config";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(args.length == 0) {
			player.openInventory(InventoryManager.uhcPlayerSettings);
		} else {
			if(args[0].equalsIgnoreCase("staff")) {
				if(player.hasPermission(Permission.OP_PERMISSION)) {
					player.openInventory(InventoryManager.uhcSettings);
				} else {
					sender.sendMessage(Msg.NO_PERMISSION);
				}
			}
		}
	}
}
