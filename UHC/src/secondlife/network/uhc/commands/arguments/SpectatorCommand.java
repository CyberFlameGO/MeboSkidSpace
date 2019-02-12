package secondlife.network.uhc.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.commands.BaseCommand;
import secondlife.network.uhc.listeners.MultiSpawnListener;
import secondlife.network.uhc.managers.GameManager;
import secondlife.network.uhc.player.UHCData;
import secondlife.network.uhc.state.GameState;
import secondlife.network.uhc.utilties.UHCUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class SpectatorCommand extends BaseCommand {

	public SpectatorCommand(UHC plugin) {
		super(plugin);

		this.command = "spectator";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(args.length == 0) {
			player.sendMessage(Color.translate("&cUsage: /mod <add|remove> <player>"));
		} else {
			if(args[0].equalsIgnoreCase("add")) {
				if(args.length == 1) {
					player.sendMessage(Color.translate("&cUsage: /mod <add|remove> <player>"));
					return;
				}
				
				Player target = Bukkit.getPlayer(args[1]);

				if(Msg.checkOffline(player, args[1])) return;

				if(UHCUtils.isPlayerInSpecMode(target)) {
					player.sendMessage(Color.translate("&c&l" + args[1] + " &cis already in spectator mode."));
					return;
				}

				UHCData uhcData = UHCData.getByName(target.getName());
				
				if(GameManager.getGameState().equals(GameState.PLAYING)) {
					if(uhcData.isAlive()) {
						uhcData.setAlive(false);
					}
				}

				plugin.getSpectatorManager().handleEnable(target);

				Msg.sendMessage("&eStaff Mode of &d" + args[1] + " &ehas been &aEnabled&e by &d" + player.getName() + "&e.", Permission.STAFF_PERMISSION);
				Msg.log(Bukkit.getConsoleSender(), "Staff Mode of " + args[1] + " has been enabled by " + player.getName() + ".");
			} else if(args[0].equalsIgnoreCase("remove")) {
				if(args.length == 1) {
					player.sendMessage(Color.translate("&cUsage: /mod <add|remove> <player>"));
					return;
				}
				
				if(GameManager.getGameState().equals(GameState.WINNER)) {
					player.sendMessage(Color.translate("&cYou can't do this now."));
					return;
				}
				
				Player target = Bukkit.getPlayer(args[1]);

				if(Msg.checkOffline(player, args[1])) return;

				if(!UHCUtils.isPlayerInSpecMode(target)) {
					player.sendMessage(Color.translate("&c&l" + args[1] + " &cisn't in a spectator mode."));
					return;
				}

				UHCUtils.disableSpecMode(target);

				if(player.hasPermission(Permission.STAFF_PERMISSION)) {
					if(GameManager.getGameState().equals(GameState.PLAYING)) {
						UHCUtils.scatterPlayer(target);
					} else {
						MultiSpawnListener.randomSpawn(target);
					}
				} else {
					target.kickPlayer(Color.translate("&cYou have been kicked by &l" + sender.getName()));
				}

				Msg.sendMessage("&eStaff Mode of &d" + args[1] + " &ehas been &cDisabled&e by &d" + player.getName() + "&e.", Permission.STAFF_PERMISSION);
				Msg.log(Bukkit.getConsoleSender(), "Staff Mode of " + args[1] + " has been disabled by " + player.getName() + ".");
			}
		}
	}
}
