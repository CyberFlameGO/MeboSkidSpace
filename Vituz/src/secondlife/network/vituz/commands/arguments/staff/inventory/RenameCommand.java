package secondlife.network.vituz.commands.arguments.staff.inventory;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class RenameCommand extends BaseCommand {

	public RenameCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "rename";
		this.permission = Permission.OP_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /rename <name>"));
		} else {
			ItemStack item = player.getItemInHand();
			
			if(item == null || item.getType() == Material.AIR) {
				player.sendMessage(Color.translate("&cYou must hold item if you want to enchant items."));
				return;
			}
			
			StringBuilder name = new StringBuilder();
			
			for(int i = 0; i < args.length; i++) {
				name.append(args[i]).append(" ");
			}
			
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(Color.translate(name.toString()));
			
			item.setItemMeta(meta);
			player.updateInventory();
			
			player.sendMessage(Color.translate("&eYou have renamed item in your hand and new name is " + name + "&e."));
		}
	}
}
