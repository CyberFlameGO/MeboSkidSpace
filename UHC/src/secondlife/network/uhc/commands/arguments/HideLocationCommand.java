package secondlife.network.uhc.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.commands.BaseCommand;
import secondlife.network.uhc.player.UHCData;
import secondlife.network.vituz.utilties.Color;

public class HideLocationCommand extends BaseCommand {

	public HideLocationCommand(UHC plugin) {
		super(plugin);
		
		this.command = "hidelocation";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if (args.length == 0) {
			UHCData uhcData = UHCData.getByName(player.getName());

			uhcData.setHideLocation(!uhcData.isHideLocation());

			player.sendMessage(Color.translate("&eYou are now " + (uhcData.isHideLocation() ? "&ahiding" : "&cnot hiding") + " &etab location."));
		}
	}

}
