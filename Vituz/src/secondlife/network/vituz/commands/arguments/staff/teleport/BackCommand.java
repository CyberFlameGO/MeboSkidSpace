package secondlife.network.vituz.commands.arguments.staff.teleport;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.StringUtils;

public class BackCommand extends BaseCommand {

	public BackCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "back";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		PlayerData data = PlayerData.getByName(player.getName());

		if(data.getBackLocation() != null) {
			player.teleport(StringUtils.destringifyLocation(data.getBackLocation()));
			data.setBackLocation(null);

			player.sendMessage(Color.translate("&eYou have teleported to previus location."));
		} else {
			player.sendMessage(Color.translate("&cPrevius location not found."));
		}
	}
}
