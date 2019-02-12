package secondlife.network.uhc.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import secondlife.network.uhc.UHC;
import secondlife.network.uhc.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class WhitelistCommand extends BaseCommand {

	public WhitelistCommand(UHC plugin) {
		super(plugin);
		
		this.command = "whitelist";
		this.permission = Permission.ADMIN_PERMISSION;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /whitelist <add|remove|on|off|clear> <player>"));
		} else if(args[0].equalsIgnoreCase("on")) {
			Bukkit.setWhitelist(true);
			
			Msg.sendMessage("&dWhitelist &ehas been &aEnabled&e.");
		} else if(args[0].equalsIgnoreCase("off")) {
			Bukkit.setWhitelist(false);
			
			Msg.sendMessage("&dWhitelist &ehas been &cDisabled&e.");
		} else if(args[0].equalsIgnoreCase("add")) {
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
			
			if(target == null) {
				sender.sendMessage("off");
				return;
			}
		
			target.setWhitelisted(true);
			
			Msg.sendMessage("&d" + target.getName() + " &ehas been &aAdded &eto whitelist by &d" + sender.getName() + "&e.", Permission.ADMIN_PERMISSION);
		} else if(args[0].equalsIgnoreCase("remove")) {
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
			
			if(target == null) {
				sender.sendMessage("off");
				return;
			}
			
			target.setWhitelisted(false);
			
			Msg.sendMessage("&d" + target.getName() + " &ehas been &cRemoved &efrom whitelist by &d" + sender.getName() + "&e.", Permission.ADMIN_PERMISSION);
		} else if(args[0].equalsIgnoreCase("clear")) {
			for(OfflinePlayer online : Bukkit.getWhitelistedPlayers()) {
				online.setWhitelisted(false);
				return;
			}
			
			Msg.sendMessage("&dAll &eplayers have been unwhitelisted by &d" + sender.getName() + "&e.");
		}	
	}
}
