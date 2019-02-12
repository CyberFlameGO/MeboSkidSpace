package secondlife.network.vituz.commands.arguments.message;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.events.PlayerMessageEvent;
import secondlife.network.vituz.data.PunishData;
import secondlife.network.vituz.punishments.Punishment;
import secondlife.network.vituz.punishments.PunishmentType;
import secondlife.network.vituz.utilties.Color;

import java.util.UUID;

public class ReplyCommand extends BaseCommand {

	public ReplyCommand(Vituz plugin) {
		super(plugin);

		this.command = "reply";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		new BukkitRunnable() {
			public void run() {
				Player player = (Player) sender;

				PunishData profile = PunishData.getByName(player.getName());
				Punishment punishment = profile.getMutedPunishment();

				if(punishment != null) {
					sender.sendMessage(PunishmentType.MUTE.getMessage().replace("%DURATION%", punishment.getTimeLeft()));
					return;
				}

				UUID lastReplied = Vituz.getInstance().getEssentialsManager().getLastReplied().get(player.getUniqueId());

				Player target = lastReplied == null ? null : Bukkit.getPlayer(lastReplied);

				if(args.length < 1) {
					if(lastReplied != null) {
						if(target == null) {
							player.sendMessage(Color.translate("&cThere is no player to reply to."));
						} else {
							player.sendMessage(Color.translate("&eYou are in a conversation with " + target.getDisplayName() + "&e."));
						}
					} else {
						player.sendMessage(Color.translate("&cUsage: /reply <meessage>"));
					}

					return;
				}

				if(target == null) {
					player.sendMessage(Color.translate("&cThere is no player to reply to."));
					return;
				}

				String message = StringUtils.join(args, ' ', 0, args.length);
				PlayerMessageEvent playerMessageEvent = new PlayerMessageEvent(player, target, message, false);

				Bukkit.getPluginManager().callEvent(playerMessageEvent);

				if(!playerMessageEvent.isCancelled()) {
					playerMessageEvent.send();
				}

			}
		}.runTaskAsynchronously(this.getPlugin());
	}
}
