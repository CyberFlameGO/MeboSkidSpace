package secondlife.network.paik.commands;

import java.io.IOException;
import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.paik.Paik;
import secondlife.network.paik.handlers.CheatHandler;
import secondlife.network.paik.utils.Color;
import secondlife.network.paik.utils.LocationUtils;

public class PaikBanCommand extends zBaseCommand {

	public PaikBanCommand(Paik plugin) {
		super(plugin);

		this.command = "paikban";
		this.permission = "secondlife.op";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /paikban <player>"));
			return;
		}
		
		if(sender instanceof Player) {
			
			Player player = (Player) sender;
			
			if(args.length == 1) {	
				
				Player target = Bukkit.getPlayer(args[0]);
				
				if(target == null) {
					player.sendMessage(Color.translate("&cThat player isn't online!"));
					return;
				}
				
				if(target.hasPermission("secondlife.staff") && !player.hasPermission("secondlife.op")) {
					player.sendMessage(Color.translate("&cYou cannot ban that player!"));
					return;
				}
				
				try {
					CheatHandler.log(target, "", "BANNED WITH /PAIKBAN COMMAND BY " + player.getName(), LocationUtils.getLocation(target), target.getPing(), new DecimalFormat("##.##").format(Bukkit.spigot().getTPS()[0]));
				} catch (IOException e) {
					e.printStackTrace();
				}
				CheatHandler.handleBan(target);
			}
			return;
			
		}
			
		Player target = Bukkit.getPlayer(args[0]);
		
		if(target == null) {
			sender.sendMessage(Color.translate("&cThat player isn't online!"));
			return;
		}
		
		try {
			CheatHandler.log(target, "", "BANNED WITH /PAIKBAN COMMAND BY " + sender.getName(), LocationUtils.getLocation(target), target.getPing(), new DecimalFormat("##.##").format(Bukkit.spigot().getTPS()[0]));
		} catch (IOException e) {
			e.printStackTrace();
		}
		CheatHandler.handleBan(target);
		
		return;
	}
}