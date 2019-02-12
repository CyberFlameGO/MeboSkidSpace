package secondlife.network.hcfactions.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.vituz.utilties.Color;

public class ToggleLightningCommand extends BaseCommand {

    public ToggleLightningCommand(HCF plugin) {
		super(plugin);
		
		this.command = "togglelightning";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			HCFData data = HCFData.getByName(player.getName());
			
			if(data.isLightning()) {
				data.setLightning(false);
				
		        sender.sendMessage(Color.translate("&eYou have &cDisabled &elightning strikes on death."));
			} else {
				data.setLightning(true);
				
		        sender.sendMessage(Color.translate("&eYou have &aEnabled &elightning strikes on death."));
			}
		}
    }
}