package secondlife.network.hcfactions.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.utilties.file.UtilitiesFile;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.inventory.InventoryUtils;

import java.io.IOException;

public class FirstJoinItemsCommand extends BaseCommand {

	public FirstJoinItemsCommand(HCF plugin) {
		super(plugin);

		this.command = "firstjoinitems";
		this.permission = Permission.OP_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			this.sendUsage(player);
		} else if(args.length == 1) {
			if(args[0].equals("set")) {
				ItemStack[] items = player.getInventory().getContents();
				
				UtilitiesFile.configuration.set("first-join-items", items);
				UtilitiesFile.configuration.set("first-join-items", InventoryUtils.itemStackArrayToBase64(items));
				
				try {
					UtilitiesFile.configuration.save(UtilitiesFile.file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				player.getInventory().clear();
				
				player.sendMessage(Color.translate("&eYou have successfully set &dFirst Join &eitems."));
			} else if (args[0].equals("remove")) {
				UtilitiesFile.configuration.set("first-join-items", new ItemStack[36]);
				UtilitiesFile.configuration.set("first-join-items", "");
				
				try {
					UtilitiesFile.configuration.save(UtilitiesFile.file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				player.sendMessage(Color.translate("&eYou have successfully removed &dFirst Join &eitems."));
			}
		} else {
			this.sendUsage(player);
		}
	}

	public void sendUsage(Player player) {
		player.sendMessage(Color.translate("&cFirst Join Items - Help Commands"));
		player.sendMessage(Color.translate("&c/firstjoinitems set - Set First Join items!"));
		player.sendMessage(Color.translate("&c/firstjoinitems remove - Remove First Join items!"));
	}

}
