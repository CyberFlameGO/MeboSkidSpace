package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class SunCommand extends BaseCommand {

	public SunCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "sun";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			player.getWorld().setStorm(false);
			player.getWorld().setThunderDuration(0);
			player.getWorld().setWeatherDuration(0);
			
			player.sendMessage(Color.translate("&eYou have set &dSun&e in world named &d" + player.getWorld().getName().toUpperCase() + "&e."));
		}
	}

}
