package secondlife.network.vituz.punishments.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.PunishData;
import secondlife.network.vituz.punishments.redis.PunishPublisher;
import secondlife.network.vituz.punishments.Punishment;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class UnbanCommand extends BaseCommand {
	
    public UnbanCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "unban";
		this.permission = Permission.STAFF_PLUS_PERMISSION;
	}
    
	@Override
	public void execute(CommandSender sender, String[] args) {     
        String senderName = sender.getName();

        if(sender instanceof Player) {
            senderName = sender.getName();
		}

        if(args.length < 2) {
         	sender.sendMessage(Color.translate("&cUsage: /unban <player> <reason>"));
            return;
        }

        PunishData profile = PunishData.getByName(args[0]);

        if (!profile.isLoaded()) {
            profile.load();
        }
        
        StringBuilder sb = new StringBuilder();
      
        for(int i = 1; i < args.length; ++i) {
            sb.append(args[i]).append(" ");
        }
        
        String reason = sb.toString().trim();

        boolean silent = false;

        if(reason.contains("-silent")) {
            silent = true;
            reason = reason.replace("-silent", "");
        } else if(reason.contains("-s")) {
            silent = true;
            reason = reason.replace("-s", "");
        }

        if(!profile.isBanned()) {
        	sender.sendMessage(Color.translate("&c" + ChatColor.stripColor(profile.getName()) + " is not banned."));
            return;
        }
        
        Punishment punishment = profile.getBannedPunishment();
       
        if(punishment != null) {
            if((!sender.hasPermission(Permission.STAFF_PLUS_PERMISSION) && punishment.getAddedBy() == null) || (punishment.getAddedBy() != null && !punishment.getAddedBy().equals(senderName) && !sender.hasPermission(Permission.STAFF_PLUS_PERMISSION))) {
                sender.sendMessage(Color.translate("&cYou don't have permission to unban a player you did not ban."));
                return;
            }
            
            punishment.setRemovedAt(System.currentTimeMillis());
            punishment.setRemovedBy(senderName);
            punishment.setRemovedReason(reason);
        }

        PunishPublisher.write("undo;BAN;" + VituzAPI.getServerName() + ";" + profile.getName() + ";" + senderName + ";" + reason + ";" + VituzAPI.getServerName() + ";" + silent);

        profile.save();
    }
}
