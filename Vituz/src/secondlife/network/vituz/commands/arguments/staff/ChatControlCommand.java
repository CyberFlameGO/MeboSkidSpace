package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.NumberUtils;
import secondlife.network.vituz.utilties.Permission;

public class ChatControlCommand extends BaseCommand {

	public ChatControlCommand(Vituz plugin) {
		super(plugin);

		this.command = "chat";
		this.permission = Permission.STAFF_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /chat <delay|mute|clear> <seconds>"));
		} else {
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("delay")) {
					sender.sendMessage(Color.translate("&cUsage: /chat delay <seconds>"));
				} else if(args[0].equalsIgnoreCase("mute")) {	
					if(plugin.getChatControlManager().isMuted()) {
						plugin.getChatControlManager().setMuted(false);
						
						Msg.sendMessage("&eChat has been &aunmuted&e by &d" + sender.getName() + "&e.");
						Msg.log(Bukkit.getConsoleSender(), "Chat has been unmuted by " + sender.getName());
					} else {
						plugin.getChatControlManager().setMuted(true);
						
						Msg.sendMessage("&eChat has been &cmuted&e by &d" + sender.getName() + "&e.");
						Msg.log(Bukkit.getConsoleSender(), "Chat has been muted by " + sender.getName());
					}
				} else if(args[0].equalsIgnoreCase("clear")) {
					for(int i = 0; i < 100; i++) {
						Msg.sendMessage("");
					}
					
					Msg.sendMessage("&eChat has been cleared by &d" + sender.getName() + "&e.");
					Msg.log(Bukkit.getConsoleSender(), "Chat has been cleared by " + sender.getName() + ".");
				} else {
					sender.sendMessage(Color.translate("&cUsage: /chat <delay|mute|clear> <seconds>"));
				}
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("delay")) {
					if(!NumberUtils.isInteger(args[1])) {
						sender.sendMessage(Color.translate("&cThis must be an integer."));
						return;
					}
					
					int seconds = Integer.parseInt(args[1]);
					
					if(seconds > 100) {
						sender.sendMessage(Color.translate("&cChat limit is 100 seconds."));
						return;
					}

					plugin.getChatControlManager().setDelay(seconds);

					Msg.sendMessage("&eChat has been slowed to &d" + seconds + " &eseconds by &d" + sender.getName() + "&e.");
					Msg.log(Bukkit.getConsoleSender(), "Chat has been slowed to " + seconds + " by " + sender.getName());
				} else {
					sender.sendMessage(Color.translate("&cUsage: /chat <delay|mute|clear> <seconds>"));
				}
			} else {
				sender.sendMessage(Color.translate("&cUsage: /chat <delay|mute|clear> <seconds>"));
			}
		}
	}
}
