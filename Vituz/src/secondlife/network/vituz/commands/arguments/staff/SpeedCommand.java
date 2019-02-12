package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.NumberUtils;
import secondlife.network.vituz.utilties.Permission;

public class SpeedCommand extends BaseCommand {

	public SpeedCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "speed";
		this.permission = Permission.ADMIN_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length < 2) {
			player.sendMessage(Color.translate("&cUsage: /speed <fly|walk> <speed>"));
		} else {
			
			if(args[0].equalsIgnoreCase("fly") || args[0].equalsIgnoreCase("f")) {
				
				if(!NumberUtils.isInteger(args[1])) {
					player.sendMessage(Color.translate("&cThis must be an integer."));
					return;
				}
				
				int amount = Integer.parseInt(args[1]);
				
				if(amount < 1 || amount > 10) {
					player.sendMessage(Color.translate("&cSpeed limit is 10."));
					return;
				}

				player.setFlySpeed(amount * 0.1F);
				
				player.sendMessage(Color.translate("&eYou have set fly speed to &d" + amount + "&e."));
			} else if(args[0].equalsIgnoreCase("walk") || args[0].equalsIgnoreCase("w")) {
				if(!NumberUtils.isInteger(args[1])) {
					player.sendMessage(Color.translate("&cThis must be an integer."));
					return;
				}
				
				int amount = Integer.parseInt(args[1]);
				
				if(amount < 1 || amount > 10) {
					player.sendMessage(Color.translate("&cSpeed limit is 10."));
					return;
				}
				
				player.setWalkSpeed(amount * 0.1F);
				player.sendMessage(Color.translate("&eYou have set walk speed to &d" + amount + "&e."));
			} else {
				player.sendMessage(Color.translate("&cUsage: /speed <fly|walk> <speed>"));	
			}
			
		}
	}
}