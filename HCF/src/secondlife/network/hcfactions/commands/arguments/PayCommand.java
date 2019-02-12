package secondlife.network.hcfactions.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.utilties.JavaUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

public class PayCommand extends BaseCommand {

	public PayCommand(HCF plugin) {
		super(plugin);
		
		this.command = "pay";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length < 2) {
			player.sendMessage(Color.translate("&cUsage: /pay <player> <amount>"));
			return;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if(target == player) {
			player.sendMessage(Color.translate("&cYou can't send money to your self."));
			return;
		}
		
		if(Msg.checkOffline(player, args[0])) return;

		HCFData data = HCFData.getByName(player.getName());
		
		Integer amount = JavaUtils.tryParseInt(args[1]);

		if(amount == null) {
			player.sendMessage(Color.translate("&cInvalid Number."));
			return;
		}

		if(amount <= 0) {
			player.sendMessage(Color.translate("&cInvalid money."));
			return;
		}
		
		if(data.getBalance() < amount) {
			player.sendMessage(Color.translate("&cYou only have &l" + data.getBalance() + "$ &con your account."));
			return;
		}

		HCFData tdata = HCFData.getByName(target.getName());
		
		data.setBalance(data.getBalance() - amount);
		tdata.setBalance(tdata.getBalance() + amount);
		
		target.sendMessage(Color.translate("&d" + sender.getName() + " &ehas sent you &d" + amount + "$&e."));
		player.sendMessage(Color.translate("&eYou have succsessfully sent &d" + target.getName() + " &d$" + amount + "&e."));
	}
}