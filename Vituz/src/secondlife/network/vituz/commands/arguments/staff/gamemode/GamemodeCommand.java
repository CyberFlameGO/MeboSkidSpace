package secondlife.network.vituz.commands.arguments.staff.gamemode;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class GamemodeCommand extends BaseCommand {

	public GamemodeCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "gamemode";
		this.permission = Permission.ADMIN_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {	
		if(sender instanceof Player) {
			Player player = (Player) sender;

			if(VituzAPI.getServerName().equals("UHC") && !player.isOp()) {
				player.sendMessage(Color.translate("&cYou can't abuse here :)"));
				return;
			}

			if(args.length == 0) {
				player.sendMessage(Color.translate("&cUsage: /gamemode <C|S|A> <player>"));
			} else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("creative")) {
					player.setGameMode(GameMode.CREATIVE);
					
					player.sendMessage(Color.translate("&eYour gamemode has been updated to &dCreative&e."));
				} else if(args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("survival")) {
					player.setGameMode(GameMode.SURVIVAL);
					
					player.sendMessage(Color.translate("&eYour gamemode has been updated to &dSurvival&e."));
				} else if(args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("adventure")) {
					player.setGameMode(GameMode.ADVENTURE);
					
					player.sendMessage(Color.translate("&eYour gamemode has been updated to &dAdventure&e."));
				}
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("creative")) {
					Player target = Bukkit.getPlayer(args[1]);
					
					if(Msg.checkOffline(player, args[1])) return;
					
					target.setGameMode(GameMode.CREATIVE);
					
					Msg.log(Bukkit.getConsoleSender(), "Player " + player.getName() + " set gamemode of " + target.getName() + " to " + target.getGameMode() + ".");
					
					target.sendMessage(Color.translate("&eYour gamemode has been updated to &dCreative &eby " + player.getDisplayName() + "&e."));
					player.sendMessage(Color.translate("&eYou have updated gamemode of " + target.getDisplayName() + " &eto &dCreative&e."));
				} else if(args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("survival")) {
					Player target = Bukkit.getPlayer(args[1]);
					
					if(Msg.checkOffline(player, args[1])) return;
					
					target.setGameMode(GameMode.SURVIVAL);
					
					Msg.log(Bukkit.getConsoleSender(), "Player " + player.getName() + " set gamemode of " + target.getName() + " to " + target.getGameMode() + ".");
					
					target.sendMessage(Color.translate("&eYour gamemode has been updated to &dSurvival &eby " + player.getDisplayName() + "&e."));
					player.sendMessage(Color.translate("&eYou have updated gamemode of " + target.getDisplayName() + " &eto &dSurvival&e."));
				} else if(args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("adventure")) {
					Player target = Bukkit.getPlayer(args[1]);
					
					if(Msg.checkOffline(player, args[1])) return;
					
					target.setGameMode(GameMode.ADVENTURE);
					
					Msg.log(Bukkit.getConsoleSender(), "Player " + player.getName() + " set gamemode of " + target.getName() + " to " + target.getGameMode() + ".");
					
					target.sendMessage(Color.translate("&eYour gamemode has been updated to &dAdventure &eby " + player.getDisplayName() + "&e."));
					player.sendMessage(Color.translate("&eYou have updated gamemode of " + target.getDisplayName() + " &eto &dAdventure&e."));
				} else {
					player.sendMessage(Color.translate("Usage: /gamemode <C|S|A> <player>"));
				}
			}

			return;
		} 
		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /gamemode <C|S|A> <player>"));
		} else {
			if(args.length == 2) {
				if(args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("creative")) {
					Player target = Bukkit.getPlayer(args[1]);
					
					if(Msg.checkOffline(sender, args[1])) return;
					
					target.setGameMode(GameMode.CREATIVE);
					
					Msg.log(Bukkit.getConsoleSender(), "CONSOLE set gamemode of " + target.getName() + " to " + target.getGameMode() + ".");
					
					target.sendMessage(Color.translate("&eYour gamemode has been updated to &dCreative &eby " + Msg.CONSOLE + "&e."));
					sender.sendMessage(Color.translate("&eYou have updated gamemode of " + target.getDisplayName() + " &eto &dCreative&e."));
				} else if(args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("survival")) {
					Player target = Bukkit.getPlayer(args[1]);
					
					if(Msg.checkOffline(sender, args[1])) return;
					
					target.setGameMode(GameMode.SURVIVAL);
					
					Msg.log(Bukkit.getConsoleSender(), "CONSOLE set gamemode of " + target.getName() + " to " + target.getGameMode() + ".");
					
					target.sendMessage(Color.translate("&eYour gamemode has been updated to &dSurvival &eby " + Msg.CONSOLE + "&e."));
					sender.sendMessage(Color.translate("&eYou have updated gamemode of " + target.getDisplayName() + " &eto &dSurvival&e."));
				} else if(args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("adventure")) {
					Player target = Bukkit.getPlayer(args[1]);
					
					if(Msg.checkOffline(sender, args[1])) return;
					
					target.setGameMode(GameMode.ADVENTURE);
					
					Msg.log(Bukkit.getConsoleSender(), "CONSOLE set gamemode of " + target.getName() + " to " + target.getGameMode() + ".");
					
					target.sendMessage(Color.translate("&eYour gamemode has been updated to &dAdventure &eby " + Msg.CONSOLE + "&e."));
					sender.sendMessage(Color.translate("&eYou have updated gamemode of " + target.getDisplayName() + " &eto &dAdventure&e."));
				} else {
					sender.sendMessage(Color.translate("&cUsage: /gamemode <C|S|A> <player>"));
				}
			}
		}
	}
}