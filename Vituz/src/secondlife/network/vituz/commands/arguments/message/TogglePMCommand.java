package secondlife.network.vituz.commands.arguments.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.utilties.Color;

public class TogglePMCommand extends BaseCommand {

	public TogglePMCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "togglepm";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		PlayerData data = PlayerData.getByName(player.getName());

		data.setToggleMsg(!data.isToggleMsg());

		player.sendMessage(Color.translate("&eYou have " + (data.isToggleMsg() ? "&aEnabled" : "&cDisabled") +
				" &eprivate messages."));
	}
}