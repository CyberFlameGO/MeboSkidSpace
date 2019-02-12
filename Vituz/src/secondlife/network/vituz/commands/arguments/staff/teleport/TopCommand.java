package secondlife.network.vituz.commands.arguments.staff.teleport;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class TopCommand extends BaseCommand {

	public TopCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "top";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			player.teleport(this.teleportToTop(player.getLocation()));
			
			player.sendMessage(Color.translate("&eYou have been teleported to the top."));
		}
	}
	
	public Location teleportToTop(Location loc) {
		return new Location(loc.getWorld(), loc.getX(), loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()), loc.getZ(), loc.getYaw(), loc.getPitch());
	}
}