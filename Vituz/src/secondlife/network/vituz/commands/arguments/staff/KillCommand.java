package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class KillCommand extends BaseCommand {

	public KillCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "kill";
		this.permission = Permission.ADMIN_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {	
		if(sender instanceof Player) {
			Player player = (Player) sender;

			if(args.length == 0) {
				sender.sendMessage(Color.translate("&cUsage: /kill <player>"));
			} else {
				Player target = Bukkit.getPlayer(args[0]);

				if(Msg.checkOffline(player, args[0])) return;

				this.killPlayer(target);

				target.sendMessage(Color.translate("&eYou have been killed by " + player.getDisplayName() + "&e."));
				player.sendMessage(Color.translate("&eYou have killed " + target.getDisplayName() + "&e."));

				Msg.log(Bukkit.getConsoleSender(), "Player " + target.getName() + " was killed by " + player.getName() + ".");
			}
			
			return;
		} 
		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /kill <player>"));
		} else {
			Player target = Bukkit.getPlayer(args[0]);

			if(Msg.checkOffline(sender, args[0])) return;

			this.killPlayer(target);

			target.sendMessage(Color.translate("&eYou have been killed by " + Msg.CONSOLE + "&e."));
			sender.sendMessage(Color.translate("&eYou have killed " + target.getDisplayName() + "&e."));

			Msg.log(Bukkit.getConsoleSender(), "Player " + target.getName() + " was killed by CONSOLE.");
		}
	}
	
	public void killPlayer(Player player) {
		player.setHealth(0.0);
	}
}