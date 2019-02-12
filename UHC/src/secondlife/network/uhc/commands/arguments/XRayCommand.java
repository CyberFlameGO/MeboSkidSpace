package secondlife.network.uhc.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.commands.BaseCommand;
import secondlife.network.vituz.utilties.Permission;

public class XRayCommand extends BaseCommand {

	public XRayCommand(UHC plugin) {
		super(plugin);
		
		this.command = "xray";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		plugin.getGameManager().handleOreAlerts(player);
	}
}
