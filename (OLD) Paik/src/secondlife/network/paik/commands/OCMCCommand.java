package secondlife.network.paik.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.paik.Paik;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.handlers.data.PlayerStatsHandler;
import secondlife.network.paik.utils.Color;

public class OCMCCommand extends zBaseCommand {

	public OCMCCommand(Paik plugin) {
		super(plugin);

		this.command = "ocmc";
		this.permission = "secondlife.staff";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /ocmc <player>"));
			return;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if(target == null) {
			sender.sendMessage(Color.translate("&cThat player isn't online!"));
			return;
		}
		
		PlayerStats stats = PlayerStatsHandler.getStats(target);
		
		if(stats.isOcmc()) {
			sender.sendMessage(Color.translate("&6" + target.getName() + " &eis &ausing &eOCMC!"));
		} else {
			sender.sendMessage(Color.translate("&6" + target.getName() + " &eis &cnot using &eOCMC!"));
		}
	}
}
