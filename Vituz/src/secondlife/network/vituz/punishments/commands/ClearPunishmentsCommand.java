package secondlife.network.vituz.punishments.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.PunishData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class ClearPunishmentsCommand extends BaseCommand {
	
    public ClearPunishmentsCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "clearpunishments";
		this.permission = Permission.OP_PERMISSION;
	}
    
	@Override
	public void execute(CommandSender sender, String[] args) {     
        if(args.length == 0) {
        	sender.sendMessage(Color.translate("&cUsage: /clearpunishments <player>"));
            return;
        }
        
        Player target = Bukkit.getPlayer(args[0]);

        PunishData profile;

        if(target == null) {
            profile = PunishData.getByName(args[0]);
        } else {
            profile = PunishData.getByName(target.getName());
        }

        if (!profile.isLoaded()) {
            profile.load();
        }
        
        profile.getPunishments().clear();
        profile.save();
        
        sender.sendMessage(Color.translate("&eYou have cleared punishments of &d" + profile.getName() + "&e."));
    }
}
