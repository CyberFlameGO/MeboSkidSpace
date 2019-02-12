package secondlife.network.hcfactions.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class SaveDataCommand extends BaseCommand {

	public SaveDataCommand(HCF plugin) {
		super(plugin);

		this.command = "savedata";
		this.permission = Permission.OP_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			long l = System.currentTimeMillis();

			Bukkit.broadcastMessage(Color.translate("&a&lSaving " + RegisterHandler.getInstancee().getFactionManager().getFactions().size() + " factions..."));

			RegisterHandler.getInstancee().getFactionManager().saveFactionData();

			Bukkit.broadcastMessage(Color.translate("&a&lSaving took " + (System.currentTimeMillis() - l) + " ms!"));
		}
	}

}
