package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.StringUtils;

public class FreezeCommand extends BaseCommand {

	public FreezeCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "freeze";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			player.sendMessage(Color.translate("&cUsage: /freeze <all|player>"));
		} else {
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("server")) {
					if(!player.hasPermission(Permission.OP_PERMISSION)) {
						player.sendMessage(Msg.NO_PERMISSION);
						return;
					}

					plugin.getFreezeManager().handleFreezeServer(sender);
				} else {
					Player target = Bukkit.getPlayer(args[0]);

					if(Msg.checkOffline(player, args[0])) return;

					if(target.isOp() && !player.isOp()) {
						player.sendMessage(Msg.NO_PERMISSION);
						return;
					}

					plugin.getFreezeManager().handleFreeze(player, target);
				}
			}
		}
	}
}
