package secondlife.network.paik.commands;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.paik.Paik;
import secondlife.network.paik.utils.Color;
import secondlife.network.paik.utils.Message;
import secondlife.network.paik.utils.PasteUtils;
import net.minecraft.util.org.apache.commons.io.FileUtils;

public class LogsCommand extends zBaseCommand {

	public LogsCommand(Paik plugin) {
		super(plugin);

		this.command = "logs";
		this.permission = "secondlife.staff";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
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
					
					File log = new File(new File(this.getInstance().getDataFolder(), "logs"), args[0] + ".txt");
					
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
				player.sendMessage(Message.COMMANDS_NO_PERMISSION_MESSAGE.toString());
			}
			return;
		}
		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /logs <player>"));
			return;
		}
		
		if(args.length == 1) {
			
			File log = new File(new File(this.getInstance().getDataFolder(), "logs"), args[0] + ".txt");
			
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