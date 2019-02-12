package secondlife.network.vituz.commands.arguments.message;

import org.bukkit.command.CommandSender;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class BroadcastCommand extends BaseCommand {

	public BroadcastCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "broadcast";
		this.permission = Permission.OP_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /broadcast <message>"));
		} else {
			
			StringBuilder message = new StringBuilder();
			for(int i = 0; i < args.length; i++) {
				message.append(args[i]).append(" ");
			}
			
			Msg.sendMessage("&8[&5&lSecondLife&8] &d" + message.toString());
		}
	}
}
