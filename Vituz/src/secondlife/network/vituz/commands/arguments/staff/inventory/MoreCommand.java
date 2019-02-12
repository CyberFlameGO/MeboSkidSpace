package secondlife.network.vituz.commands.arguments.staff.inventory;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class MoreCommand extends BaseCommand {

	public MoreCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "more";
		this.permission = Permission.ADMIN_PERMISSION;
		this.forPlayerUseOnly = false;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		ItemStack item = player.getItemInHand();
		
		if(item == null || item.getType() == Material.AIR) {
			player.sendMessage(Color.translate("&cYou must hold item if you want to enchant items."));
			return;
		}
		
		if(item.getAmount() >= 64) {
			player.sendMessage(Color.translate("&cYour item is already stacked."));
			return;
		}
		
		item.setAmount(64);
		
		player.updateInventory();
		
		player.sendMessage(Color.translate("&eYou have stacked item in your hand to &d64&e."));
	}
}
