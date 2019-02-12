package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class FlyCommand extends BaseCommand {

	public FlyCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "fly";
		this.permission = Permission.STAFF_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {	
		if(sender instanceof Player) {
			Player player = (Player) sender;

			if(args.length == 0) {
				this.flyPlayer(player);
			} else {
				if(player.hasPermission(Permission.OP_PERMISSION)) {
					Player target = Bukkit.getPlayer(args[0]);

					if (Msg.checkOffline(player, args[0])) return;

					this.flyTarget(player, target);
				} else {
					player.sendMessage(Msg.NO_PERMISSION);
				}
			}
			
			return;
		} 
		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /fly <player>"));
		} else {
			Player target = Bukkit.getPlayer(args[0]);

			if(Msg.checkOffline(sender, args[0])) return;

			this.flyTarget(sender, target);
		}
	}
	
	public void flyPlayer(Player player) {
		if(player.getAllowFlight()) {
			player.setAllowFlight(false);
			
			player.sendMessage(Color.translate("&eYou have &cDisabled&e fly."));
		} else {
			player.setAllowFlight(true);
			
			player.sendMessage(Color.translate("&eYou have &aEnabled&e fly."));

		}
	}
	
	public void flyTarget(CommandSender sender, Player target) {
		if(target.getAllowFlight()) {
			target.setAllowFlight(false);
			
			if(sender instanceof Player) {
				Msg.log(Bukkit.getConsoleSender(), "Player " + target.getName() + "'s flight mode has been disabled by " + sender.getName() + ".");
				
				target.sendMessage(Color.translate("&eYour flight has been &cDisabled&e by &d" + sender.getName() + "&e."));
				sender.sendMessage(Color.translate("&eYou have &cDisabled&e fly of &d" + target.getDisplayName() + "&e."));
			} else {
				Msg.log(Bukkit.getConsoleSender(), "Player " + target.getName() + "'s flight mode has been disabled by CONSOLE.");
				
				target.sendMessage(Color.translate("&eYour flight has been &cDisabled&e by &d" + Msg.CONSOLE + "&e."));
				sender.sendMessage(Color.translate("&eYou have &cDisabled&e fly of &d" + target.getDisplayName() + "&e."));
			}
		} else {
			target.setAllowFlight(true);
			
			if(sender instanceof Player) {
				Msg.log(Bukkit.getConsoleSender(), "Player " + target.getName() + "'s flight mode has been enabled by " + sender.getName() + ".");
				
				target.sendMessage(Color.translate("&eYour flight has been &aEnabled&e by &d" + sender.getName()));
				sender.sendMessage(Color.translate("&eYou have &aEnabled&e fly of &d" + target.getDisplayName() + "&e."));
			} else {
				Msg.log(Bukkit.getConsoleSender(), "Player " + target.getName() + "'s flight mode has been enabled by CONSOLE.");
				
				target.sendMessage(Color.translate("&eYour flight has been &aEnabled&e by &d" + Msg.CONSOLE + "&e."));
				sender.sendMessage(Color.translate("&eYou have &aEnabled&e fly of &d" + target.getDisplayName() + "&e."));
			}

		}
	}
}