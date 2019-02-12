package secondlife.network.hcfactions.factions.commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.utilties.JavaUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class FactionSetBalanceCommand extends SubCommand {

	public FactionSetBalanceCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "setbalance" };
		this.permission = Permission.OP_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if (args.length < 3) {
			sender.sendMessage(Color.translate("&cUsage: /f setbalance <faction> <balance>"));
			return;
		}

		if (Msg.checkOffline(player, args[1])) return;

		Player target = Bukkit.getPlayerExact(args[1]);

		PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(target.getName());

		if (playerFaction == null) {
			sender.sendMessage(HCFUtils.FACTION_NOT_FOUND);
			return;
		}

		if (!args[2].chars().allMatch(Character::isDigit)) {
			sender.sendMessage(Color.translate("&cInvalid number"));
			return;
		}

		int factionBalance = playerFaction.getBalance();
		int addedBalance = Integer.valueOf(args[2]);
		int newBalance = factionBalance + addedBalance;

		playerFaction.setBalance(newBalance);
		playerFaction.broadcast("&d" + player.getName() + " &ehas given your faction &d$" + JavaUtils.format(addedBalance) + "&e!");

		Msg.sendMessage("&d" + player.getName() + " &ehas given the faction &d" + playerFaction.getName() + " &d$" + addedBalance + "&e!", Permission.OP_PERMISSION);
	}
}