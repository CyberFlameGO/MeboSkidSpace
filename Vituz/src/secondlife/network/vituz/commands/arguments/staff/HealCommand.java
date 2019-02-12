package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class HealCommand extends BaseCommand {

	public HealCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "heal";
		this.permission = Permission.STAFF_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {	
		if(sender instanceof Player) {
			Player player = (Player) sender;

			if(args.length == 0) {
				this.healPlayer(player);

				player.sendMessage(Color.translate("&eYou have been healed."));

				Msg.log(Bukkit.getConsoleSender(), "Player " + player.getName() + " healed his self.");
			} else {
				if(args[0].equalsIgnoreCase("all")) {
					if(player.hasPermission(Permission.OP_PERMISSION)) {
						for(Player online : Bukkit.getOnlinePlayers()) {
							this.healPlayer(online);
						}

						Msg.sendMessage("&eAll online players have been healed by " + player.getDisplayName() + "&e.");
						Msg.log(Bukkit.getConsoleSender(), "Player " + player.getName() + " healed all online players.");
					} else {
						player.sendMessage(Msg.NO_PERMISSION);
					}
				} else {
					Player target = Bukkit.getPlayer(args[0]);

					if(Msg.checkOffline(player, args[0])) return;

					this.healPlayer(target);

					target.sendMessage(Color.translate("&eYou have been healed by " + player.getDisplayName() + "&e."));
					player.sendMessage(Color.translate("&eYou have healed " + target.getDisplayName() + "&e."));

					Msg.log(Bukkit.getConsoleSender(), "Player " + target.getName() + " was healed by " + player.getName() + ".");
				}
			}
			
			return;
		} 
		
		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /heal <player|all>"));
		} else {
			if(args[0].equalsIgnoreCase("all")) {
				for(Player online : Bukkit.getOnlinePlayers()) {
					this.healPlayer(online);
				}

				Msg.sendMessage("&eAll online players have been healed by " + Msg.CONSOLE + "&e.");
				Msg.log(Bukkit.getConsoleSender(), "CONSOLE healed all online players.");
			} else {
				Player target = Bukkit.getPlayer(args[0]);

				if(Msg.checkOffline(sender, args[0])) return;

				this.healPlayer(target);

				target.sendMessage(Color.translate("&eYou have been healed by " + Msg.CONSOLE + "&e."));
				sender.sendMessage(Color.translate("&eYou have healed " + target.getDisplayName() + "&e."));

				Msg.log(Bukkit.getConsoleSender(), "Player " + target.getName() + " was healed by CONSOLE.");
			}
		}
	}
	
	public void healPlayer(Player player) {
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setSaturation(10);
		player.setFireTicks(0);
		
		for(PotionEffect effects : player.getActivePotionEffects()) {
			player.removePotionEffect(effects.getType());
		}
	}
}