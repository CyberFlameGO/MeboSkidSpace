package secondlife.network.hcfactions.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.utilties.file.UtilitiesFile;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.StringUtils;

public class SetCommand extends BaseCommand {

	public SetCommand(HCF plugin) {
		super(plugin);

		this.command = "set";
		this.permission = Permission.OP_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			this.sendUsage(player);
		} else if(args[0].equalsIgnoreCase("endexit")) {
			UtilitiesFile.configuration.set("World-Spawn.end-exit", StringUtils.stringifyLocation(player.getLocation()));
			UtilitiesFile.save();
			
			sender.sendMessage(Color.translate("&aYou have succsesfuly set End Exit!"));
		} else if(args[0].equalsIgnoreCase("endspawn")) {
			UtilitiesFile.configuration.set("World-Spawn.end-spawn", StringUtils.stringifyLocation(player.getLocation()));
			UtilitiesFile.save();
			
			sender.sendMessage(Color.translate("&aYou have succsesfuly set End Spawn!"));
		} else if(args[0].equalsIgnoreCase("spawn")) {
			UtilitiesFile.configuration.set("World-Spawn.world-spawn", StringUtils.stringifyLocation(player.getLocation()));
			UtilitiesFile.save();
			
			sender.sendMessage(Color.translate("&aYou have succsesfuly set World Spawn!"));
		} else if(args[0].equalsIgnoreCase("netherspawn")) {
			UtilitiesFile.configuration.set("World-Spawn.nether-spawn", StringUtils.stringifyLocation(player.getLocation()));
			UtilitiesFile.save();
			
			sender.sendMessage(Color.translate("&aYou have succsesfuly set Nether Spawn!"));
		} else if(args[0].equalsIgnoreCase("eotwffa")) {
			UtilitiesFile.configuration.set("World-Spawn.eotw-ffa", StringUtils.stringifyLocation(player.getLocation()));
			UtilitiesFile.save();
			
			sender.sendMessage(Color.translate("&aYou have succsesfuly set EOTW-FFA spawn point!"));
		} 
	}
	
	public void sendUsage(CommandSender sender) {
		sender.sendMessage(Color.translate("&cSet - Help Commands:"));
		sender.sendMessage(Color.translate("&c/set spawn - Set world spawn!"));
		sender.sendMessage(Color.translate("&c/set netherspawn - Set nether spawn!"));
		sender.sendMessage(Color.translate("&c/set endspawn - Set end spawn!"));
		sender.sendMessage(Color.translate("&c/set endexit - Set end exit!"));
		sender.sendMessage(Color.translate("&c/set eotwffa - Set eotwffa spawn point!"));
	}
}
