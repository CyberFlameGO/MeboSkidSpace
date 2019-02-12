package secondlife.network.vituz.commands.arguments.message;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.events.PlayerMessageEvent;
import secondlife.network.vituz.data.PunishData;
import secondlife.network.vituz.punishments.Punishment;
import secondlife.network.vituz.punishments.PunishmentType;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

public class MessageCommand extends BaseCommand {

	public MessageCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "message";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		PunishData profile = PunishData.getByName(player.getName());
		Punishment punishment = profile.getMutedPunishment();

		if(punishment != null) {
			sender.sendMessage(PunishmentType.MUTE.getMessage().replace("%DURATION%", punishment.getTimeLeft()));
			return;
		}

		if(args.length < 2) {
			player.sendMessage(Color.translate("&cUsage: /msg <player> <message>"));
		} else {
			Player target = Bukkit.getPlayer(args[0]);
			
			if(Msg.checkOffline(player, args[0])) return;
			
			if(target == player) {
				player.sendMessage(Color.translate("&cYou can't message your self."));
				return;
			}
			
			String message = StringUtils.join(args, ' ', 1, args.length);
			PlayerMessageEvent playerMessageEvent = new PlayerMessageEvent(player, target, message, false);
			
			Bukkit.getPluginManager().callEvent(playerMessageEvent);
			
			if(!playerMessageEvent.isCancelled()) {
				playerMessageEvent.send();
			}
		}
	}
}
