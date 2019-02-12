package secondlife.network.hcfactions.staff.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.staff.handlers.VanishHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.staff.OptionType;
import secondlife.network.vituz.utilties.Color;

public class VanishCommand extends BaseCommand {

	public VanishCommand(HCF plugin) {
		super(plugin);
		
		this.command = "vanish";
		this.permission = "secondlife.staff";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			if(VanishHandler.vanishedPlayers.contains(player.getUniqueId())) {
				VanishHandler.unvanishPlayer(player);
			} else {
				VanishHandler.vanishPlayer(player);
			}
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("options")) {
				player.sendMessage(Color.translate("&eYou can toggle: &d" + HCFUtils.getVanishOptionsList(player)));
			} else if(args[0].equalsIgnoreCase("toggle") || args[0].equalsIgnoreCase("t")) {
				this.sendUsage(player);
			} else {
				this.sendUsage(player);
			}
		} else if(args.length == 2) {
			if(args[0].equalsIgnoreCase("toggle") || args[0].equalsIgnoreCase("t")) {
				if(VanishHandler.vanishedPlayers.contains(player.getUniqueId())) {
					String option = args[1];
					boolean exists = false;
					
					for(OptionType optionType : OptionType.values()) {
						if(optionType.getName().equalsIgnoreCase(option)) {
							exists = true;
							
							if(optionType.getPlayers().contains(player.getUniqueId())) {
								optionType.getPlayers().remove(player.getUniqueId());
								
								player.sendMessage(Color.translate("&eYou have toggled off: &c" + optionType.getName()));
							} else {
								optionType.getPlayers().add(player.getUniqueId());
								
								player.sendMessage(Color.translate("&eYou have toggle on: &a" + optionType.getName()));
							}
						}
					}
					
					if(!exists) {
						player.sendMessage(Color.translate("&eThere is no option named &d" + option + "&e!"));
						player.sendMessage(Color.translate("&eYou can toggle: &d" + HCFUtils.getVanishOptionsList(player)));
					}
				} else {
					player.sendMessage(Color.translate("&eCan't toggle Options while not &dVanished&e!"));
				}
			} else {
				this.sendUsage(player);
			}
		} else {
			this.sendUsage(player);
		}
	}

	public void sendUsage(Player player) {
		player.sendMessage(Color.translate("&cVanish - Help Commands"));
		player.sendMessage(Color.translate("&c/vanish options - See all options."));
		player.sendMessage(Color.translate("&c/vanish toggle <option> - Toggle options."));
	}
}
