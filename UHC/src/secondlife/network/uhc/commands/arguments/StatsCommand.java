
package secondlife.network.uhc.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.commands.BaseCommand;
import secondlife.network.uhc.player.UHCData;
import secondlife.network.vituz.utilties.Color;

public class StatsCommand extends BaseCommand {

	public StatsCommand(UHC plugin) {
		super(plugin);

		this.command = "stats";
		this.forPlayerUseOnly = true;
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			if(!plugin.getGameManager().isStats()) {
				sender.sendMessage(Color.translate("&cStats is currently disabled."));
				return;
			}

			UHCData data = UHCData.getByName(player.getName());

			player.sendMessage(Color.translate("&eYour stats:"));
			player.sendMessage(Color.translate("&eWins: &d" + data.getWins()));
			player.sendMessage(Color.translate("&ePlayed: &d" + data.getPlayed()));
			player.sendMessage(Color.translate("&eKills: &d" + data.getTotalKills()));
			player.sendMessage(Color.translate("&eDeaths: &d" + data.getDeaths()));
			player.sendMessage(Color.translate("&eKD: &d" + data.getKD()));
			player.sendMessage(Color.translate("&eKill Streak: &d" + data.getKillStreak()));
			player.sendMessage(Color.translate("&eDiamonds Mined: &d" + data.getTotalDiamondsMined()));
		} else {
            OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(args[0]);
            
            if(!target.hasPlayedBefore()) {
                sender.sendMessage(ChatColor.RED + "This player never played on this server.");
                return;
            }

			UHCData data = UHCData.getByName(target.getName());

			player.sendMessage(Color.translate("&eStats of &d" + args[0] + "&e!"));
			player.sendMessage(Color.translate("&eWins: &d" + data.getWins()));
			player.sendMessage(Color.translate("&ePlayed: &d" + data.getPlayed()));
			player.sendMessage(Color.translate("&eKills: &d" + data.getTotalKills()));
			player.sendMessage(Color.translate("&eDeaths: &d" + data.getDeaths()));
			player.sendMessage(Color.translate("&eKD: &d" + data.getKD()));
			player.sendMessage(Color.translate("&eKill Streak: &d" + data.getKillStreak()));
			player.sendMessage(Color.translate("&eDiamonds Mined: &d" + data.getTotalDiamondsMined()));
		}
		
	}
}