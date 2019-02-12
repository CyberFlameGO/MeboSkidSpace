package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class NightCommand extends BaseCommand {

	public NightCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "night";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			player.getWorld().setTime(14000);
			
			player.sendMessage(Color.translate("&eYou have set &dNight&e in world named &d" + player.getWorld().getName().toUpperCase() + "&e."));
		}
	}

}
