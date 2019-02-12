package secondlife.network.vituz.commands.arguments.message;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

import java.util.List;

public class IgnoreCommand extends BaseCommand {

	public IgnoreCommand(Vituz plugin) {
		super(plugin);

		this.command = "ignore";
		this.forPlayerUseOnly = true;

	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		PlayerData data = PlayerData.getByName(player.getName());
		
		List<String> ignoring = data.getIgnoring();

		if(args.length == 0) {
			player.sendMessage(Color.translate("&cUsage: /ignore <add|remove|list> <player>"));
		} else {
			if(args[0].equalsIgnoreCase("remove")) {
				if(args.length < 1) {
					player.sendMessage(Color.translate("&cUsage: /ignore <add|remove|list> <player>"));
					return;
				}
							
				player.sendMessage(Color.translate("&eYou are " + (ignoring.remove(args[1]) ? new StringBuilder().append(ChatColor.RED).append("not").toString() : new StringBuilder().append(ChatColor.GREEN).append("no longer").toString()) + " &eignoring &d" + args[1] + "&e."));
			} else if(args[0].equalsIgnoreCase("list")) {
				if(ignoring.isEmpty()) {
					player.sendMessage(Color.translate("&cYou are not ignoring anyone."));
					return;
				}
				
				player.sendMessage(Color.translate("&eYou are ignoring &7(&d" + ignoring.size() + "&7) &eMembers: &7[&d" + StringUtils.join(data.getIgnoring(), ", ") + "&7]&e."));
			} else if(args[0].equalsIgnoreCase("add")) {
				if(args.length < 2) {
					player.sendMessage(Color.translate("&cUsage: /ignore <add|remove|list> <player>"));
					return;
				}
				
				Player target = Bukkit.getPlayer(args[1]);

				if(Msg.checkOffline(player, args[1])) return;

				if(target == player) {
					sender.sendMessage(Color.translate("&cYou may not ignore yourself."));
					return;
				}

				if(!player.isOp() && target.hasPermission(Permission.STAFF_PERMISSION)) {
					sender.sendMessage(Msg.NO_PERMISSION);
					return;
				}

				if(ignoring.add(target.getName())) {
					player.sendMessage(Color.translate("&eYou are now ignoring " + target.getDisplayName() + "&e."));
				} else {
					player.sendMessage(Color.translate("&eYou are already ignoring someone named " + target.getDisplayName() + "&e."));
				}
			}
		}
	}
}
