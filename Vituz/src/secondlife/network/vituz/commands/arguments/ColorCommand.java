package secondlife.network.vituz.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Permission;

public class ColorCommand extends BaseCommand {

	public ColorCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "color";
		this.permission = Permission.COLOR_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {	
		Player player = (Player) sender;
 		
		if(args.length == 0) {
			player.openInventory(plugin.getColorsManager().getInventory());
		}
	}
}
