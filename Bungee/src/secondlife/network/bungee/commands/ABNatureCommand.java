package secondlife.network.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import secondlife.network.bungee.handlers.PlayerHandler;
import secondlife.network.bungee.utils.Color;

public class ABNatureCommand extends Command {
	
	public ABNatureCommand() {
		super("antibotnature", "secondlife.op", "ab");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /antibot <spam|length|fully|edo|lengthedo>"));
			return;
		}
		
		if(args[0].equalsIgnoreCase("spam")) {
			if(PlayerHandler.spam) {
				PlayerHandler.spam = false;
				sender.sendMessage(Color.translate("&cAntiBot spam disabled!"));
			} else {
				PlayerHandler.spam = true;
				sender.sendMessage(Color.translate("&cAntiBot spam enabled!"));
			}
		}
		
		if(args[0].equalsIgnoreCase("length")) {
			if(args.length == 1) {
				sender.sendMessage(Color.translate("&cUsage: /antibot length <length>"));
				return;
			}
			
			int length = Integer.parseInt(args[1]);
			
			if(length > 16 || length < 4) {
				sender.sendMessage(Color.translate("&cMust be between 3-16"));
				return;
			}
			
			PlayerHandler.blockLengh = length;
			sender.sendMessage(Color.translate("&cSucessfully set lenght to " + length));
		}
		
		if(args[0].equalsIgnoreCase("lengthedo")) {
			if(args.length == 1) {
				sender.sendMessage(Color.translate("&cUsage: /antibot length <length>"));
				return;
			}
			
			int length = Integer.parseInt(args[1]);
			
			if(length > 16 || length < 4) {
				sender.sendMessage(Color.translate("&cMust be between 3-16"));
				return;
			}
			
			PlayerHandler.block = length;
			sender.sendMessage(Color.translate("&cSucessfully set lenght to " + length));
		}
		
		if(args[0].equalsIgnoreCase("fully")) {
			if(PlayerHandler.enabled) {
				PlayerHandler.enabled = false;
				sender.sendMessage(Color.translate("&cAntiBot fully disabled!"));
			} else {
				PlayerHandler.enabled = true;
				sender.sendMessage(Color.translate("&cAntiBot fully enabled!"));
			}
		}
		
		if(args[0].equalsIgnoreCase("edo")) {
			if(PlayerHandler.edo) {
				PlayerHandler.edo = false;
				sender.sendMessage(Color.translate("&cAntiBot fully disabled!"));
			} else {
				PlayerHandler.edo = true;
				sender.sendMessage(Color.translate("&cAntiBot fully enabled!"));
			}
		}
	}
}
