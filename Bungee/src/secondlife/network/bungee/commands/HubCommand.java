package secondlife.network.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import secondlife.network.bungee.utils.Color;

public class HubCommand extends Command {
	
	public HubCommand() {
		super("hub", "", "lobby" );
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(Color.translate("&cPlayer use only!"));
			return;
		}
		
		ProxiedPlayer player = (ProxiedPlayer) sender;
		
		if(player.getServer().getInfo().getName().equalsIgnoreCase("Hub")) {
			player.sendMessage(Color.translate("&cYou are already connected to the hub!"));
			return;
		}
		
		ServerInfo target = ProxyServer.getInstance().getServerInfo("Hub");
		player.connect(target);
	}
}