package secondlife.network.paik.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.paik.Paik;
import secondlife.network.paik.utils.Color;

public class PingCommand extends zBaseCommand {

	public PingCommand(Paik plugin) {
		super(plugin);

		this.command = "ping";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(args.length == 0) {
				player.sendMessage(Color.translate("&eYour ping: &6" + player.getPing()));
				return;
			}
			
			Player target = Bukkit.getPlayer(args[0]);
			
			if(target == null) {
				player.sendMessage(Color.translate("&cThat player isn't online!"));
				return;
			}
			
			player.sendMessage(Color.translate("&6" + target.getName() + "'s &eping: &6" + target.getPing()));
			return;
		}

		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /ping <player>"));
			return;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if(target == null) {
			sender.sendMessage(Color.translate("&cThat player isn't online!"));
			return;
		}
		
		sender.sendMessage(Color.translate("&6" + target.getName() + "'s &eping: &6" + target.getPing()));
		return;
	}
}
