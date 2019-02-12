package secondlife.network.vituz.commands.arguments.staff.inventory;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.NumberUtils;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.StringUtils;

public class EnchantCommand extends BaseCommand {

	public EnchantCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "enchant";
		this.permission = Permission.ADMIN_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length < 2) {	
			player.sendMessage(Color.translate("&cUsage: /enchant <enchantment> <level>"));
		} else {
			ItemStack item = player.getItemInHand();
			
			if(item == null || item.getType() == Material.AIR) {
				player.sendMessage(Color.translate("&cYou must hold item if you want to enchant items."));
				return;
			}
			
			if(!NumberUtils.isInteger(args[1])) {
				player.sendMessage(Color.translate("&cThis must be an integer."));
				return;
			}
			
			int level = Integer.parseInt(args[1]);
			
			if(level < 0) {
				player.sendMessage(Color.translate("&cEnchantment must bee positive."));
				return;
			}
			
			if (level > 7 && !player.isOp()) {
				player.sendMessage(Color.translate("&cEnchant limit can't be more than 7."));
				return;
			}
			
			
			String enchantment = StringUtils.getEnchantment(args[0]);
			
			if(level == 0) {
				if(item.containsEnchantment(Enchantment.getByName(enchantment))) {
					item.removeEnchantment(Enchantment.getByName(enchantment));
					
					player.sendMessage(Color.translate("&eYou have removed enchantmnet &d" + args[0].toUpperCase() + "&e."));
				} else {
					player.sendMessage(Color.translate("&eThat item doesn't containts enchantment " + args[0].toUpperCase() + "&e."));
				}
			} else {
				item.addUnsafeEnchantment(Enchantment.getByName(enchantment), level);

				player.sendMessage(Color.translate("&eYou have enchanted item &d" + item.getType() + "&e."));
			}
		}
	}
}