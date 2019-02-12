package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class GodCommand extends BaseCommand {

	public GodCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "god";
		this.permission = Permission.ADMIN_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {	
		if(sender instanceof Player) {
			Player player = (Player) sender;

			if(args.length == 0) {
				this.godPlayer(player);
			} else {
				if(player.hasPermission(Permission.OP_PERMISSION)) {
					Player target = Bukkit.getPlayer(args[0]);

					if(Msg.checkOffline(player, args[0])) return;

					this.godTarget(player, target);
				} else {
					player.sendMessage(Msg.NO_PERMISSION);
				}
			}
		
			return;
		} 
		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /god <player>"));
		} else {
			Player target = Bukkit.getPlayer(args[0]);

			if(Msg.checkOffline(sender, args[0])) return;

			this.godTarget(sender, target);
		}
	}
	
	public void godPlayer(Player player) {
		PlayerData data = PlayerData.getByName(player.getName());

		if(data.isGod()) {
			data.setGod(false);
			player.setNoDamageTicks(0);
			
			player.sendMessage(Color.translate("&eYou have &cDisabled&e god mode."));
		} else {
			data.setGod(true);
			player.setNoDamageTicks(Integer.MAX_VALUE);
			
			player.sendMessage(Color.translate("&eYou have &aEnabled&e god mode."));
		}
	}
	
	public void godTarget(CommandSender sender, Player target) {
		PlayerData data = PlayerData.getByName(target.getName());

		if(data.isGod()) {
			data.setGod(false);
			target.setNoDamageTicks(0);
			
			if(sender instanceof Player) {
				Msg.log(Bukkit.getConsoleSender(), "Player " + target.getName() + "'s god mode has been disabled by " + sender.getName() + ".");
				
				target.sendMessage(Color.translate("&eYour god mode has been &cDisabled&e by &d" + sender.getName() + "&e."));
				sender.sendMessage(Color.translate("&eYou have &cDisabled&e god of &d" + target.getDisplayName() + "&e."));
			} else {
				Msg.log(Bukkit.getConsoleSender(), "Player " + target.getName() + "'s god mode has been disabled by CONSOLE.");
				
				target.sendMessage(Color.translate("&eYour god mode has been &cDisabled&e by &d" + Msg.CONSOLE + "&e."));
				sender.sendMessage(Color.translate("&eYou have &cDisabled&e god of &d" + target.getDisplayName() + "&e."));
			}
		} else {
			data.setGod(true);
			target.setNoDamageTicks(Integer.MAX_VALUE);
			
			if(sender instanceof Player) {
				Msg.log(Bukkit.getConsoleSender(), "Player " + target.getName() + "'s god mode has been enabled by " + sender.getName() + ".");
				
				target.sendMessage(Color.translate("&eYour god mode has been &aEnabled&e by &d" + sender.getName()));
				sender.sendMessage(Color.translate("&eYou have &aEnabled&e god of &d" + target.getDisplayName() + "&e."));
			} else {
				Msg.log(Bukkit.getConsoleSender(), "Player " + target.getName() + "'s god mode has been enabled by CONSOLE.");
				
				target.sendMessage(Color.translate("&eYour god mode has been &aEnabled&e by &d" + Msg.CONSOLE + "&e."));
				sender.sendMessage(Color.translate("&eYou have &aEnabled&e god of &d" + target.getDisplayName() + "&e."));
			}

		}
	}
}