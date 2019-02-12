package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class DayCommand extends BaseCommand {

	public DayCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "day";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			player.getWorld().setTime(0);

			player.sendMessage(Color.translate("&eYou have set &dDay&e in world named &d" + player.getWorld().getName().toUpperCase() + "&e."));
		}
	}

}
