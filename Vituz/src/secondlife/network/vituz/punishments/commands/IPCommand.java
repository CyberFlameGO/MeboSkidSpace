package secondlife.network.vituz.punishments.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.PunishData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class IPCommand extends BaseCommand {
	
    public IPCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "ip";
		this.permission = Permission.OP_PERMISSION;
	}
    
	@Override
	public void execute(CommandSender sender, String[] args) {     
        if(args.length == 0) {
        	sender.sendMessage(Color.translate("&cUsage: /ip <player>"));
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
        
        if(profile.getAddress() != null) {
            sender.sendMessage(Color.translate("&eIP addres of &a" + profile.getName() + " &eis &d" + profile.getAddress() + "&e."));
        } else {
        	sender.sendMessage(Color.translate("&cThat player doesn't have ip addres set yet."));
        }
    }
}
