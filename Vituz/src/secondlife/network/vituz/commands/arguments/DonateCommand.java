package secondlife.network.vituz.commands.arguments;

import org.bukkit.command.CommandSender;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;

public class DonateCommand extends BaseCommand {

	public DonateCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "donate";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&eYou can donate at &dstore.secondlife.network"));
		}
	}
}
