package secondlife.network.vituz.commands.arguments.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class SocialspyCommand extends BaseCommand {

	public SocialspyCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "socialspy";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		PlayerData data = PlayerData.getByName(player.getName());

		data.setSocialSpy(!data.isSocialSpy());

		player.sendMessage(Color.translate("&eYou have " + (data.isSocialSpy() ? "&aEnabled" : "&cDisabled") +
				" &esocialspy."));
	}
}