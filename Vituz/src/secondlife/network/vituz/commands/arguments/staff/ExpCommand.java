package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.NumberUtils;
import secondlife.network.vituz.utilties.Permission;

public class ExpCommand extends BaseCommand {

	public ExpCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "exp";
		this.permission = Permission.ADMIN_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {	
		if(sender instanceof Player) {
			Player player = (Player) sender;

			if(args.length == 0) {
				player.sendMessage(Color.translate("&cUsage: /exp <player> <amount>"));
			} else {
				Player target = Bukkit.getPlayer(args[0]);

				if(Msg.checkOffline(player, args[0])) return;
				
				int amount = Integer.parseInt(args[1]);
				
				if(!NumberUtils.isInteger(args[1])) {
					player.sendMessage(Color.translate("&cThis must be an integer."));
					return;
				}
				
				if(amount > 500) {
					player.sendMessage(Color.translate("&cExp limit is 500."));
					return;
				}

				this.expPlayer(target, amount);

				target.sendMessage(Color.translate("&eYour exp has been set to &d" + amount + " &eby " + player.getDisplayName() + "&e."));
				player.sendMessage(Color.translate("&eYou have set exp of " + target.getDisplayName() + " &eto &d" + amount + "&e."));

				Msg.log(Bukkit.getConsoleSender(),"Player " + target.getName() + "'s exp has been set to " + amount + " by " + player.getName() + ".");
			}
			
			return;
		} 
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /exp <player> <amount>"));
		} else {
			Player target = Bukkit.getPlayer(args[0]);

			if(Msg.checkOffline(sender, args[0])) return;
			
			int amount = Integer.parseInt(args[1]);
			
			if(!NumberUtils.isInteger(args[1])) {
				sender.sendMessage(Color.translate("&cThis must be an integer."));
				return;
			}
			
			if(amount > 500) {
				sender.sendMessage(Color.translate("&cExp limit is 500."));
				return;
			}

			this.expPlayer(target, amount);

			target.sendMessage(Color.translate("&eYour exp has been set to &d" + amount + " &eby " + Msg.CONSOLE + "&e."));
			sender.sendMessage(Color.translate("&eYou have set exp of " + target.getDisplayName() + " &eto &d" + amount + "&e."));

			Msg.log(Bukkit.getConsoleSender(),"Player " + target.getName() + "'s exp has been set to " + amount + " by CONSOLE.");
		}
	}
	
	public void expPlayer(Player player, int amount) {
		player.setLevel(amount);
	}
}