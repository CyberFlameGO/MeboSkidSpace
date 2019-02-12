package secondlife.network.vituz.commands.arguments.console;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.ServerUtils;

public class BungeeCommand extends BaseCommand {

	public BungeeCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "bungeecommand";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(Msg.NO_PERMISSION);
            return;
        }
		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /bungeecommand <command>"));
		} else {
			StringBuilder bungeeCmd = new StringBuilder();
            
            for(int i = 0; i < args.length; i++) {
            	bungeeCmd.append(args).append(" ");
            }
            
            ServerUtils.commandToBungee(bungeeCmd.toString());
		}
	}
}
