package secondlife.network.vituz.commands.arguments;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

public class PlaytimeCommand extends BaseCommand {
	
	public PlaytimeCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "playtime";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(args.length == 0) {
			long l = player.getStatistic(Statistic.PLAY_ONE_TICK);

			player.sendMessage(Color.translate("&eYour playtime is &d" + DurationFormatUtils.formatDurationWords(l * 50L, true, true) + " &eon this server."));
		} else {
			Player target = Bukkit.getPlayer(args[0]);

			if(Msg.checkOffline(player, args[0])) return;

			long l = target.getStatistic(Statistic.PLAY_ONE_TICK);

			player.sendMessage(Color.translate("&d" + target.getName() + "'s &eplaytime is &d" + DurationFormatUtils.formatDurationWords(l * 50L, true, true) + " &eon this server."));
		}
	}
}
