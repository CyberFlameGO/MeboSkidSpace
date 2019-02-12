package secondlife.network.vituz.commands.arguments;

import org.bukkit.command.CommandSender;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;

public class DiscordCommand extends BaseCommand {

	public DiscordCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "discord";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&eDiscord &ddiscord.gg/tN8Tugp"));
		}
	}
}
