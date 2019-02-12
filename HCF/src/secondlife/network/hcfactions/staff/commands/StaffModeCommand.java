package secondlife.network.hcfactions.staff.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.staff.handlers.StaffModeHandler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class StaffModeCommand extends BaseCommand {

	public StaffModeCommand(HCF plugin) {
		super(plugin);

		this.command = "staff";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(StaffModeHandler.isInStaffMode(player)) {
			StaffModeHandler.disableStaffMode(player);
			
			player.sendMessage(Color.translate("&eYour staff mode has been &cDisabled&e."));
		} else {
			StaffModeHandler.enableStaffMode(player);

		player.sendMessage(Color.translate("&eYour staff mode has been &aEnabled&e."));
		}
	}
}
