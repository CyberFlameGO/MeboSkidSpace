package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.ServerUtils;

public class StaffJoinCommand extends BaseCommand {

	public StaffJoinCommand(Vituz plugin) {
		super(plugin);

		this.command = "staffjoin";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(args.length == 0) {
			player.sendMessage(Color.translate("&cUsage: /staffjoin <serverName>"));
		} else {
			ServerUtils.sendToServer(player, args[0]);
		}
	}
}
