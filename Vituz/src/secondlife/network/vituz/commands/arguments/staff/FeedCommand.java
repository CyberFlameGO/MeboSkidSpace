package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class FeedCommand extends BaseCommand {

	public FeedCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "feed";
		this.permission = Permission.STAFF_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {	
		if(sender instanceof Player) {
			Player player = (Player) sender;

			if(args.length == 0) {
				this.feedPlayer(player);

				player.sendMessage(Color.translate("&eYou have been fed."));

				Msg.log(Bukkit.getConsoleSender(), "Player " + player.getName() + " fed his self.");
			} else {
				if(args[0].equalsIgnoreCase("all")) {
					if(player.hasPermission(Permission.OP_PERMISSION)) {
						for(Player online : Bukkit.getOnlinePlayers()) {
							this.feedPlayer(online);
						}

						Msg.sendMessage("&eAll online players have been fed by " + player.getDisplayName() + "&e.");
						Msg.log(Bukkit.getConsoleSender(), "Player " + player.getName() + " fed all online players.");
					} else {
						player.sendMessage(Msg.NO_PERMISSION);
					}
				} else {
					Player target = Bukkit.getPlayer(args[0]);

					if(Msg.checkOffline(player, args[0])) return;

					this.feedPlayer(target);

					target.sendMessage(Color.translate("&eYou have been fed by " + player.getDisplayName() + "&e."));
					player.sendMessage(Color.translate("&eYou have fed " + target.getDisplayName() + "&e."));

					Msg.log(Bukkit.getConsoleSender(), "Player " + target.getName() + " was fed by " + player.getName() + ".");
				}
			}
			
			return;
		} 
		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /feed <player|all>"));
		} else {
			if(args[0].equalsIgnoreCase("all")) {
				for(Player online : Bukkit.getOnlinePlayers()) {
					this.feedPlayer(online);
				}

				Msg.sendMessage("&eAll online players have been fed by " + Msg.CONSOLE + "&e.");
				Msg.log(Bukkit.getConsoleSender(), "CONSOLE fed all online players.");
			} else {
				Player target = Bukkit.getPlayer(args[0]);

				if(Msg.checkOffline(sender, args[0])) return;

				this.feedPlayer(target);

				target.sendMessage(Color.translate("&eYou have been fed by " + Msg.CONSOLE + "&e."));
				sender.sendMessage(Color.translate("&eYou have fed " + target.getDisplayName() + "&e."));

				Msg.log(Bukkit.getConsoleSender(), "Player " + target.getName() + " was fed by CONSOLE.");
			}
		}
	}
	
	public void feedPlayer(Player player) {
		player.setFoodLevel(20);
		player.setSaturation(10);
	}
}