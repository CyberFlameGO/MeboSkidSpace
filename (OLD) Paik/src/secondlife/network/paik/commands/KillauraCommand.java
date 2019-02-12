package secondlife.network.paik.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.paik.Paik;
import secondlife.network.paik.checks.combat.Killaura;
import secondlife.network.paik.utils.Color;

public class KillauraCommand extends zBaseCommand {

	public KillauraCommand(Paik plugin) {
		super(plugin);

		this.command = "killaura";
		this.permission = "secondlife.op";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /killaura <player>"));
			return;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if(target == null) {
			sender.sendMessage(Color.translate("&cThat player isn't online!"));
			return;
		}
		
		if(PaikCommand.disabled.contains("KillAura_Bot")) {
			sender.sendMessage(Color.translate("&cKillaura Bot check is currently disabled, therefore you can't use this command!"));
			return;
		}
			
		Killaura.teleportBot(target);
		sender.sendMessage(Color.translate("&eSucessfully teleported Killaura Bot to &6" + target.getName()));
	}
}
