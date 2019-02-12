package secondlife.network.vituz.commands.arguments.staff.inventory;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class GiveYourSelfCommand extends BaseCommand {

	public GiveYourSelfCommand(Vituz plugin) {
		super(plugin);

		this.command = "giveyourself";
		this.permission = Permission.ADMIN_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		String amount = "";

		Player player = (Player) sender;

		if(args.length == 0) {
			player.sendMessage(Color.translate("&cUsage: /giveyourself <item> <amount>"));
		} else {
			if(plugin.getItemDB().getItem(args[0]) == null) {
				player.sendMessage(Color.translate("&cItem or ID not found."));
				return;
			}

			if(args.length == 1) {
				if(!player.getInventory().addItem(new ItemStack[] { plugin.getItemDB().getItem(args[0], plugin.getItemDB().getItem(args[0]).getMaxStackSize()) }).isEmpty()) {
					player.sendMessage(Color.translate("&cYour inventory is full."));
					return;
				}

				amount = plugin.getItemDB().getItem(args[0]).getMaxStackSize() + "";
			}

			if(args.length == 2) {
				if(!player.getInventory().addItem(new ItemStack[] { plugin.getItemDB().getItem(args[0], Integer.parseInt(args[1])) }).isEmpty()) {
					player.sendMessage(Color.translate("&cYour inventory is full."));
					return;
				}

				amount = args[1];
			}

			Msg.log(Bukkit.getConsoleSender(), "Player " + player.getName() + " has given his self " + amount + " of " + plugin.getItemDB().getName(plugin.getItemDB().getItem(args[0])) + ".");

			player.sendMessage(Color.translate("&eYou have given your self &d" + amount + " &eof &d" + plugin.getItemDB().getName(plugin.getItemDB().getItem(args[0])) + "&e."));
		}
	}
}
