package secondlife.network.vituz.commands.arguments.staff.inventory;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class ClearCommand extends BaseCommand {

	public ClearCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "clear";
		this.permission = Permission.STAFF_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {	
		if(sender instanceof Player) {
			Player player = (Player) sender;

			if(args.length == 0) {
				this.clearPlayer(player);

				player.sendMessage(Color.translate("&eYou have cleared your inventory."));
			} else {
				if(player.hasPermission(Permission.OP_PERMISSION)) {
					Player target = Bukkit.getPlayer(args[0]);

					if(Msg.checkOffline(player, args[0])) return;

					this.clearPlayer(target);

					target.sendMessage(Color.translate("&eYour inventory was cleared by " + player.getDisplayName() + "&e."));
					player.sendMessage(Color.translate("&eYou have cleared inventory of " + target.getDisplayName() + "&e."));

					Msg.log(Bukkit.getConsoleSender(),"Player " + target.getName() + "'s inventory was cleared by " + player.getName() + ".");
				} else {
					player.sendMessage(Msg.NO_PERMISSION);
				}
			}
			
			return;
		} 
		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /clear <player>"));
		} else {
			Player target = Bukkit.getPlayer(args[0]);

			if(Msg.checkOffline(sender, args[0])) return;

			this.clearPlayer(target);

			target.sendMessage(Color.translate("&eYour inventory was cleared by " + Msg.CONSOLE + "&e."));
			sender.sendMessage(Color.translate("&eYou have cleared inventory of " + target.getDisplayName() + "&e."));

			Msg.log(Bukkit.getConsoleSender(),"Player " + target.getName() + "'s inventory was cleared by CONSOLE.");
		}
	}
	
	public void clearPlayer(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
	}
}