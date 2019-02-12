package secondlife.network.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import secondlife.network.bungee.handlers.SilentHandler;
import secondlife.network.bungee.utils.Color;

public class SilentCommand extends Command {
	
	public SilentCommand() {
		super("silent", "secondlife.op", "filter");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(new TextComponent(Color.translate("&cPlayer use only!")));
			return;
		}
		
		ProxiedPlayer player = (ProxiedPlayer) sender;
		
		if(SilentHandler.silent.contains(player.getUniqueId())) {
			SilentHandler.silent.remove(player.getUniqueId());
			player.sendMessage(new TextComponent(Color.translate("&eYou have &cCisabled &esilent mode.")));
			return;
		}
		
		SilentHandler.silent.add(player.getUniqueId());
		player.sendMessage(new TextComponent(Color.translate("&eYou have &aEnabled &esilent mode.")));
	}
}