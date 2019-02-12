package secondlife.network.uhc.commands.arguments;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import lombok.Getter;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

public class HealthCommand extends BaseCommand {
	
    @Getter private DecimalFormat formatter;

	public HealthCommand(UHC plugin) {
		super(plugin);
		
		this.formatter = new DecimalFormat("#.00");
		
		this.command = "health";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			player.sendMessage(Color.translate("&eYour health is &d" + this.getHeartsLeftString(player.getHealth()) + "&e."));
		} else {
			Player target = Bukkit.getPlayer(args[0]);
		
			if(Msg.checkOffline(player, args[0])) return;
			
			player.sendMessage(Color.translate("&d" + target.getName() + "'s &ehealth is &d" + this.getHeartsLeftString(target.getHealth()) + "&e."));
		}
		
	}
	
	public String getHeartsLeftString(double healthLeft) {
		return Color.translate("&d" + Math.ceil(healthLeft) / 2D + "&4&l " + Msg.HEART);
	}
}
