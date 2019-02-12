package secondlife.network.hcfactions.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.NumberUtils;
import secondlife.network.vituz.utilties.Permission;

public class BalanceCommand extends BaseCommand {

	public BalanceCommand(HCF plugin) {
		super(plugin);
		
		this.command = "balance";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			HCFData data1 = HCFData.getByName(player.getName());
			
			player.sendMessage(Color.translate("&eYour balance is &d$" + data1.getBalance() + "&e!"));
		} else {
			if(args.length == 1) {
				Player target = Bukkit.getPlayer(args[0]);
				
				if(Msg.checkOffline(player, args[0])) return;

				HCFData data = HCFData.getByName(target.getName());
				
				player.sendMessage(Color.translate("&eBalance of &d" + args[0] + " &eis &d$" + data.getBalance() + "&e!"));
			} else if(args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("add")) {
				if(!player.hasPermission(Permission.OP_PERMISSION)) {
					player.sendMessage(Msg.NO_PERMISSION);
					return;
				}
 				
				Player target = Bukkit.getPlayer(args[1]);
				
				if(Msg.checkOffline(player, args[1])) return;
				
				if(!NumberUtils.isInteger(args[2])) {
					player.sendMessage(Color.translate("&cNumber must be integer!"));
					return;
				}
				
				int amount = Integer.parseInt(args[2]);
				
				if(amount > 100000) {
					player.sendMessage(Color.translate("&cBalance limit is 100000!"));
					return;
				}

				HCFData data = HCFData.getByName(target.getName());
				
				data.setBalance(data.getBalance() + amount);
				
				player.sendMessage(Color.translate("&eYou have set balance of &d" + args[1] + " &eto &d$" + data.getBalance() + "&e!"));
				target.sendMessage(Color.translate("&eYour balance is now &d" + data.getBalance() + "&e!"));
			}
		}
	}
}
