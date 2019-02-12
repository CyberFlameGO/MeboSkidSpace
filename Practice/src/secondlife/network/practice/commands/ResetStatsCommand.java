package secondlife.network.practice.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.kit.Kit;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.utilties.PlayerUtil;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class ResetStatsCommand extends Command {

	private final Practice plugin = Practice.getInstance();

	public ResetStatsCommand() {
		super("resetstats");

		setUsage(CC.RED + "Usage: /resetstats [player]");
	}

	@Override
	public boolean execute(CommandSender sender, String s, String[] args) {
		if(sender instanceof Player) {
			if(!PlayerUtil.testPermission(sender, Permission.OP_PERMISSION)) {
				return true;
			}
		}

		if(args.length == 0) {
			sender.sendMessage(CC.RED + "Usage: /resetstats <player>");
			return true;
		}

		Player target = this.plugin.getServer().getPlayer(args[0]);

		if(Msg.checkOffline(sender, args[0])) return true;

		PracticeData playerData = PracticeData.getByName(target.getName());
		for(Kit kit : this.plugin.getKitManager().getKits()) {
			playerData.setElo(kit.getName(), PracticeData.DEFAULT_ELO);
			playerData.setLosses(kit.getName(), 0);
			playerData.setWins(kit.getName(), 0);
		}

		playerData.setPremiumElo(PracticeData.DEFAULT_ELO);
		playerData.setPremiumLosses(0);
		playerData.setPremiumWins(0);

		sender.sendMessage(CC.PRIMARY + "You reset " + CC.SECONDARY + target.getName() + CC.PRIMARY + "'s stats.");
		return true;
	}

}
