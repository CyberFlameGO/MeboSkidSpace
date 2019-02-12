package secondlife.network.uhc.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.commands.BaseCommand;
import secondlife.network.uhc.utilties.UHCUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.NumberUtils;
import secondlife.network.vituz.utilties.Permission;

public class FlatCommand extends BaseCommand {

	public FlatCommand(UHC plugin) {
		super(plugin);
		
		this.command = "flat";
		this.permission = Permission.OP_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /flat <amount>"));
		} else {
			int amount = Integer.parseInt(args[0]);
			
			if(!NumberUtils.isInteger(args[0])) {
				sender.sendMessage(Color.translate("&cThis must be an integer."));
				return;
			}
			
			if(amount > 200) {
				sender.sendMessage(Color.translate("&cFlat limit is 200."));
				return;
			}
			
			UHCUtils.flat(amount + 5, Bukkit.getWorld("uhc_world")); 
			UHCUtils.buildWalls(Material.BEDROCK, 10, Bukkit.getWorld("uhc_world"), amount, amount + 5);
			
			sender.sendMessage(Color.translate("&eYou have set flat zone to &d" + amount + " &eblocks."));
		}
	}
}
