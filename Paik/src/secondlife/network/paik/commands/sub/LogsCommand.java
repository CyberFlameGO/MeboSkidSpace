package secondlife.network.paik.commands.sub;

import org.apache.commons.io.FileUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.commands.PaikCommand;
import secondlife.network.paik.utilties.command.Command;
import secondlife.network.paik.utilties.command.CommandArgs;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.PasteUtils;

import java.io.File;
import java.io.IOException;

public class LogsCommand extends PaikCommand {

	@Command(name = "logs", permission = "secondlife.staff")
	public void onCommand(CommandArgs command) {
		CommandSender sender = command.getSender();
		String[] args = command.getArgs();

		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.getName().equalsIgnoreCase("Pof")
				|| player.getName().equalsIgnoreCase("Crystaled")
				|| player.getName().equalsIgnoreCase("relaxkid")
				|| player.getName().equalsIgnoreCase("HiMyNameIsTechy")
				|| player.getName().equalsIgnoreCase("R4pexay")
				|| player.isOp()) {
				
				if(args.length == 0) {
					player.sendMessage(Color.translate("&cUsage: /logs <player>"));
					return;
				}
				
				if(args.length == 1) {
					File log = new File(new File(Paik.getInstance().getDataFolder(), "logs"), args[0] + ".txt");
					
					if(!log.exists()) {
						player.sendMessage(Color.translate("&cThat player has no logs!"));
						return;
					}
					
					try {
						String content = FileUtils.readFileToString(log);
						
						player.sendMessage(Color.translate("&cLogs of player &4" + args[0] + " &cpasted at &4" + new PasteUtils(content, args[0], PasteUtils.Visibility.UNLISTED, PasteUtils.Expire.TEN_MINUTES, PasteUtils.Language.TEXT).upload()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				player.sendMessage(Msg.NO_PERMISSION);
			}

			return;
		}
		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /logs <player>"));
			return;
		}
		
		if(args.length == 1) {
			File log = new File(new File(Paik.getInstance().getDataFolder(), "logs"), args[0] + ".txt");
			
			if(!log.exists()) {
				sender.sendMessage(Color.translate("&cThat player has no logs!"));
				return;
			}
			
			try {
				String content = FileUtils.readFileToString(log);
				
				sender.sendMessage(Color.translate("&cLogs of player &4" + args[0] + " &cpasted at &4" + new PasteUtils(content, args[0], PasteUtils.Visibility.UNLISTED, PasteUtils.Expire.TEN_MINUTES, PasteUtils.Language.TEXT).upload()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return;
	}
}