package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class SeenCommand extends BaseCommand {

	public SeenCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "seen";
		this.permission = Permission.STAFF_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /seen <player>"));
		} else {
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

			if(target == null || !target.hasPlayedBefore()) {
				sender.sendMessage(Color.translate("&cThat player has never played before!"));
				return;
			}

			PlayerData data;

			if(target.isOnline()) {
				data = PlayerData.getByName(target.getName());

				if(data.getLastSeen() != null) {
					sender.sendMessage(Color.translate("&d" + target.getName() + " &ehas been &aonline &e since &a" + data.getLastSeen()));
				} else {
					sender.sendMessage(Color.translate("&eThat player has never played before!"));
				}
			} else {
				data = PlayerData.getByName(args[0]);

				if(data.getLastSeen() != null) {
					sender.sendMessage(Color.translate("&d" + args[0] + " &ehas been &coffline &e since &a" + data.getLastSeen()));
				} else {
					sender.sendMessage(Color.translate("&eThat player has never played before!"));
				}
			}

			/*OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

			if(offlinePlayer.isOnline()) {
				sender.sendMessage(Color.translate("&d" + offlinePlayer.getName() + " &eis currently &aonline&e."));
			} else {
				if(offlinePlayer.getLastPlayed() != 0) {
					long millis = System.currentTimeMillis() - offlinePlayer.getLastPlayed();

					long second = (millis / 1000) % 60;
					long minute = (millis / (1000 * 60)) % 60;
					long hour = (millis / (1000 * 60 * 60)) % 24;
					long day = (millis / (1000 * 60 * 60 * 24));

					sender.sendMessage(Color.translate("&d" + offlinePlayer.getName() + " &ewas last seen &a" + (day > 0 ? day + " days " : "") + (hour > 0 ? hour + " hours " : "") + (minute > 0 ? minute + " minutes " : "") + (second > 0 ? second + " seconds " : "") + " &eago."));
				} else {
					sender.sendMessage(Color.translate("&cFailed to find player."));
				}
			}*/
		}
	}
}