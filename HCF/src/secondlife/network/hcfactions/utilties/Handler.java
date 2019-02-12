package secondlife.network.hcfactions.utilties;

import lombok.Getter;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.classes.utils.ArmorClassHandler;
import secondlife.network.hcfactions.commands.arguments.FocusCommand;
import secondlife.network.hcfactions.handlers.CombatLoggerHandler;
import secondlife.network.hcfactions.handlers.MapKitHandler;
import secondlife.network.hcfactions.timers.AppleHandler;
import secondlife.network.hcfactions.timers.EnderpearlHandler;
import secondlife.network.hcfactions.timers.LogoutHandler;
import secondlife.network.hcfactions.staff.handlers.StaffModeHandler;
import secondlife.network.hcfactions.staff.handlers.VanishHandler;

@Getter
public class Handler {
	
	private HCF instance;
		
	public Handler(HCF plugin) {
		this.instance = plugin;
	}
	
	public static void disable() {
		ArmorClassHandler.disable();
		FocusCommand.focus.clear();

		CombatLoggerHandler.disable();
		MapKitHandler.disable();
		StaffModeHandler.disable();
		VanishHandler.disable();
		
		AppleHandler.disable();
		EnderpearlHandler.disable();
		LogoutHandler.disable();
	}
}
