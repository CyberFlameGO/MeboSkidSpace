package secondlife.network.vituz.commands.arguments.staff.teleport;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.NumberUtils;
import secondlife.network.vituz.utilties.Permission;

public class TeleportpositionCommand extends BaseCommand {

	public TeleportpositionCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "teleportposition";
		this.permission = Permission.STAFF_PLUS_PERMISSION;
		this.forPlayerUseOnly = false;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length < 3) {
			player.sendMessage(Color.translate("&cUsage: /tppos <x> <y> <z>"));
		} else {
			if(!NumberUtils.isInteger(args[0])) {
				player.sendMessage(Color.translate("&cThis must be an integer."));
				return;
			}
			
			if(!NumberUtils.isInteger(args[1])) {
				player.sendMessage(Color.translate("&cThis must be an integer."));
				return;
			}
			
			if(!NumberUtils.isInteger(args[2])) {
				player.sendMessage(Color.translate("&cThis must be an integer."));
				return;
			}
			
			int x = (int) Integer.parseInt(args[0]);
			int y = (int) Integer.parseInt(args[1]);
			int z = (int) Integer.parseInt(args[2]);
			
			Location loc = new Location(player.getWorld(), x, y, z);
			
			if (x > 30000000 || y > 30000000 || z > 30000000 || x < -30000000 || y < -30000000 || z < -30000000) {
				sender.sendMessage(Color.translate("&cTeleportposition maximum teleport coordinates are 30000000."));
				return;
			}
			
			player.teleport(loc);
			
			player.sendMessage(Color.translate("&eYou have been teleported to &ex&d" + x + "&7, &ey&d" + y + "&7, &ez&d" + z + "&e."));
		}
	}
}