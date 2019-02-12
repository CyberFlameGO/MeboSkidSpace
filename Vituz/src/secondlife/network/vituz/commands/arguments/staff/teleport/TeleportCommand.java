package secondlife.network.vituz.commands.arguments.staff.teleport;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class TeleportCommand extends BaseCommand {

	public TeleportCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "teleport";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			player.sendMessage(Color.translate("&cUsage: /tp <player>"));
		} else {
			Player target = Bukkit.getPlayer(args[0]);
			
			if(Msg.checkOffline(player, args[0])) return;
			
			player.teleport(target.getLocation());
			
			player.sendMessage(Color.translate("&eYou have been teleported to " + target.getDisplayName() + "&e."));
		}
	}
}