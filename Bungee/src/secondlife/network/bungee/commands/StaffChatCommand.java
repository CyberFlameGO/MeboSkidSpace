package secondlife.network.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import secondlife.network.bungee.handlers.StaffChatHandler;
import secondlife.network.bungee.utils.Color;

public class StaffChatCommand extends Command {

	public StaffChatCommand() {
		super("staffchat", "secondlife.staff", "sc", "schat", "ac");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(Color.translate("&cPlayer use only!"));
			return;
		}
		
		ProxiedPlayer player = (ProxiedPlayer) sender;

		if(!player.hasPermission("secondlife.staff")) {
			player.sendMessage(Color.translate("&cNo Permission"));
			return;
		}
		
		/*if(player.getServer().getInfo().getName().equalsIgnoreCase("Hub")) {
			player.sendMessage(Color.translate("&cStaff Chat is disabled on this server."));
			return;
		}*/
	
		if(StaffChatHandler.staff.contains(player.getUniqueId())) {
			StaffChatHandler.staff.remove(player.getUniqueId());
			player.sendMessage(Color.translate("&eStaff Chat has been &cDisabled&e."));
		} else {
			StaffChatHandler.staff.add(player.getUniqueId());
			player.sendMessage(Color.translate("&eStaff Chat has been &aEnabled&e."));
		}
	}
}