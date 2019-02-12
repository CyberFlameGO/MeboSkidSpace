package secondlife.network.hcfactions.commands.arguments;

import org.bukkit.command.CommandSender;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.handlers.MapKitHandler;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.file.ConfigFile;
import secondlife.network.hcfactions.utilties.file.LimitersFile;
import secondlife.network.hcfactions.utilties.file.UtilitiesFile;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class zSecondLifeCommand extends BaseCommand {

	public zSecondLifeCommand(HCF plugin) {
		super(plugin);
		
		this.command = "secondlife";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			sendInfo(sender);
		} else if(args[0].equalsIgnoreCase("reload")) {
			if(sender.hasPermission(Permission.OP_PERMISSION)) {
				RegisterHandler.getInstancee().getFactionManager().saveFactionData();
				
				ConfigFile.load();
				LimitersFile.load();
				UtilitiesFile.save();
				MapKitHandler.loadInventory();
				
				sender.sendMessage(Color.translate("&a&lSecondLife Core has been reloaded!"));
			 } else {
				 sender.sendMessage(Msg.NO_PERMISSION);
			 }
		}
	}
	
	public static void sendInfo(CommandSender sender) {
		sender.sendMessage(Color.translate("&7&m-------------------------"));
		sender.sendMessage("");
		sender.sendMessage(Color.translate("&6&lSecondLife HCFCore"));
		sender.sendMessage(Color.translate(" &7* &eVersion&7: &d" + HCF.getInstance().getDescription().getVersion()));
		sender.sendMessage(Color.translate(" &7* &eAuthors"));
		sender.sendMessage(Color.translate("   &7- &dVISUAL_"));
		sender.sendMessage(Color.translate("   &7- &dItsNature"));
		sender.sendMessage("");
		sender.sendMessage(Color.translate("&7&m-------------------------"));
	}

}