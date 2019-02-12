package secondlife.network.hcfactions.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.timers.LogoutHandler;
import secondlife.network.vituz.utilties.Color;

public class LogoutCommand extends BaseCommand {

	public LogoutCommand(HCF plugin) {
		super(plugin);

		this.command = "logout";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(HCFData.getByName(player.getName()).isEvent()) {
			player.sendMessage(Color.translate("&cYou can't do this in your current state."));
			return;
		}

		if(RegisterHandler.getInstancee().getFactionManager().getFactionAt(player.getLocation()).isSafezone()) {
			sender.sendMessage(Color.translate("&cYou can't use this command in safezone claims."));
			return;
		}
		
		if(LogoutHandler.teleporting.containsKey(player)) {
			sender.sendMessage(Color.translate("&cYou are already logging out."));
			return;
		}

		LogoutHandler.createLogout(player);
		
		LogoutHandler.teleporting.put(player,Bukkit.getScheduler().scheduleSyncRepeatingTask(this.getInstance(), new Runnable() {
			int i = 30;

			public void run() {
				if (i != 0) {
					player.playSound(player.getLocation(), Sound.NOTE_BASS_DRUM, 1f, 1f);
					player.sendMessage(
							Color.translate("&e&lLogging out... &ePlease wait &c" + i + " &eseconds."));
					i--;
					return;
				}

				player.setMetadata("LogoutCommand", new FixedMetadataValue(HCF.getInstance(), Boolean.TRUE));
				player.kickPlayer(Color.translate("&cYou have been safely logged out of the server!"));

				Bukkit.getScheduler().cancelTask(LogoutHandler.teleporting.get(player));
				LogoutHandler.teleporting.remove(player);

			}
		}, 0, 20));
	}
}
