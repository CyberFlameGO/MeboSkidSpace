package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class KickallCommand extends BaseCommand {

	public KickallCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "kickall";
		this.permission = Permission.OP_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(args.length == 0) {
				player.setMaxHealth(((Damageable) player).getMaxHealth() + 2.0);
				sender.sendMessage(Color.translate("&cUsage: /kickall <reason>"));
			} else {
				StringBuilder message = new StringBuilder();
				for(int i = 0; i < args.length; i++) {
					message.append(args[i]).append(" ");
				}
				
				for(Player online : Bukkit.getOnlinePlayers()) {
					if(!online.hasPermission(Permission.STAFF_PERMISSION)) {
						online.kickPlayer(Color.translate(message.toString()));
					}
				}
				
				Msg.sendMessage("&eAll online players were kicked by " + player.getDisplayName() + " &efor &d" + message.toString() + "&e.");
			}
			
			return;
		}
		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /kickall <reason>"));
		} else {
			StringBuilder message = new StringBuilder();
			for(int i = 0; i < args.length; i++) {
				message.append(args[i]).append(" ");
			}
			
			for(Player online : Bukkit.getOnlinePlayers()) {
				if(!online.hasPermission(Permission.STAFF_PERMISSION)) {
					online.kickPlayer(Color.translate(message.toString()));
				}
			}
			
			Msg.sendMessage("&eAll online players were kicked by " + Msg.CONSOLE + " &efor &d" + message.toString() + "&e.");
		}
	}
}
