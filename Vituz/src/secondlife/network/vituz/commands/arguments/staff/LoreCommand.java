package secondlife.network.vituz.commands.arguments.staff;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

import java.util.ArrayList;
import java.util.List;

public class LoreCommand extends BaseCommand {

	public LoreCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "lore";
		this.permission = Permission.OP_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
 		
		if(args.length < 1) {
			player.sendMessage(Color.translate("&cUsage: /lore <lore>"));
			return;
		}
		
	    ItemStack stack = player.getItemInHand();
	    
	    if(stack == null) {
			player.sendMessage(Color.translate("&cYou must hold item if you want to enchant items."));
	    	return;
	    }
	    
	    ItemMeta meta = stack.getItemMeta();
	    
	    String text = Color.translate(StringUtils.join(args, ' ', 0, args.length));
	    List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>(2);
	    
	    lore.add(text);
	    meta.setLore(lore);
	    
	    stack.setItemMeta(meta);
			
		player.sendMessage(Color.translate("&aYou have added lore. Check item."));
	}

}
