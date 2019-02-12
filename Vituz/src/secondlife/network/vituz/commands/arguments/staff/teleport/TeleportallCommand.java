package secondlife.network.vituz.commands.arguments.staff.teleport;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class TeleportallCommand extends BaseCommand {

	public TeleportallCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "teleportall";
		this.permission = Permission.OP_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			for(Player online : Bukkit.getOnlinePlayers()) {
				online.teleport(player.getLocation());
			}
			
			Msg.sendMessage("&eAll online players have been teleported to " + player.getDisplayName() + "&e.");
		}
	}
}