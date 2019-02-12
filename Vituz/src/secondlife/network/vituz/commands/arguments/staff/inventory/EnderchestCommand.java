package secondlife.network.vituz.commands.arguments.staff.inventory;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class EnderchestCommand extends BaseCommand {

	public EnderchestCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "enderchest";
		this.permission = Permission.ADMIN_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(args.length == 0) {
			this.openEnderchest(player);

			player.sendMessage(Color.translate("&eYou have opened enderchest."));

			Msg.log(Bukkit.getConsoleSender(), "Player " + player.getName() + " has opened enderchest.");
		} else {
			if(player.hasPermission(Permission.OP_PERMISSION)) {
				Player target = Bukkit.getPlayer(args[0]);

				if(Msg.checkOffline(player, args[0])) return;

				player.openInventory(target.getEnderChest());

				player.sendMessage(Color.translate("&eYou have opened enderchest of " + target.getDisplayName() + "&e."));

				Msg.log(Bukkit.getConsoleSender(), "Player " + player.getName() + " opened enderchest of " + target.getName() + ".");
			} else {
				player.sendMessage(Msg.NO_PERMISSION);
			}
		}
	}
	
	public void openEnderchest(Player player) {
		player.openInventory(player.getEnderChest());
	}
}