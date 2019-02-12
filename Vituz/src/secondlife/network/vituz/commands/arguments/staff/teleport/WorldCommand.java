package secondlife.network.vituz.commands.arguments.staff.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class WorldCommand extends BaseCommand {

	public WorldCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "world";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			player.sendMessage(Color.translate("&cUsage: /world <world>"));
		} else {
			World world = Bukkit.getWorld(args[0]);
			Location loc = new Location(world, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
			
			if(world == null) {
				player.sendMessage(Color.translate("&cWorld '" + args[0] + "' doesn't exists."));
				return;
			}
			
			player.teleport(loc);
			
			player.sendMessage(Color.translate("&eYou have been teleported to &d" + player.getWorld().getName().toUpperCase() + "&e."));
		}
	}
}