package secondlife.network.hcfactions.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.handlers.MapKitHandler;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class MapKitCommand extends BaseCommand {
	
	public MapKitCommand(HCF plugin) {
		super(plugin);

		this.command = "mapkit";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			player.openInventory(MapKitHandler.mapKitInv);
		} else if(args.length == 1) {
			if(args[0].equals("edit")) {
				if(!player.hasPermission(Permission.OP_PERMISSION)) {
					player.sendMessage(Msg.NO_PERMISSION);
					return;
				}
				
				MapKitHandler.editingMapKit.add(player.getUniqueId());
				player.openInventory(MapKitHandler.mapKitInv);
			}
		}
    }
}
