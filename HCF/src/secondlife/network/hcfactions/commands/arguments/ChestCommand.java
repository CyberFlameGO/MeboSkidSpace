package secondlife.network.hcfactions.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.vituz.utilties.Color;

public class ChestCommand extends BaseCommand {

	public ChestCommand(HCF plugin) {
		super(plugin);

		this.command = "chest";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			if(!HCFConfiguration.kitMap) {
				player.sendMessage(Color.translate("&cThis command can only be used when kitmap is enabled!"));
				return;
			}

			if(HCFData.getByName(player.getName()).isEvent()) {
				player.sendMessage(Color.translate("&cYou can't do this in your current state."));
				return;
			}
			
			if(!RegisterHandler.getInstancee().getFactionManager().getFactionAt(player.getLocation()).isSafezone()) {
				player.sendMessage(Color.translate("&cThis command can only be used in Safe-Zone claims!"));
				return;
			}
			
			player.openInventory(player.getEnderChest());
		}
	}

}
