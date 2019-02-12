package secondlife.network.vituz.commands.arguments.staff.teleport;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class TeleporthereCommand extends BaseCommand {

	public TeleporthereCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "teleporthere";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			player.sendMessage(Color.translate("&cUsage: /tphere <player>"));
		} else {
			Player target = Bukkit.getPlayer(args[0]);
			
			if(Msg.checkOffline(player, args[0])) return;
			
			target.teleport(player.getLocation());
			
			player.sendMessage(Color.translate("&eYou have been teleported " + target.getDisplayName() + " &eto your self."));
		}
	}
}