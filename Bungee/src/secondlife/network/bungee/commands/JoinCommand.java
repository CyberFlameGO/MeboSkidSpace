package secondlife.network.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import secondlife.network.bungee.utils.Color;

public class JoinCommand extends Command {
	
	public JoinCommand() {
		super("join", "", "play" );
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(Color.translate("&cPlayer use only!"));
			return;
		}
		
		ProxiedPlayer player = (ProxiedPlayer) sender;
		
		if(player.getServer().getInfo().getName().equalsIgnoreCase("Hub")) {
			player.sendMessage(Color.translate("&cThis command is disabled in hub!"));
			return;
		}
		
		if(args.length == 0) {
			player.sendMessage(Color.translate("&cUsage: /join <server>"));
			player.sendMessage(Color.translate("&cThis command is used to connect to UHCMeetup servers! (UHCMeetup-1, UHCMeetup-2...)"));
			return;
		}
		
		if(args.length == 1) {
			
			ServerInfo target = ProxyServer.getInstance().getServerInfo(args[0]);
			
			if(target == null) {
				player.sendMessage(Color.translate("&cThat server doesn't exist!"));
				return;
			}
			
			if(!args[0].toLowerCase().contains("uhcmeetup-")) {
				player.sendMessage(Color.translate("&cYou can only connect to UHCMeetup servers with this command!"));
				return;
			}
			
			player.connect(target);
			return;
		}
	}
}