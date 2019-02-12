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
import secondlife.network.vituz.punishments.PunishmentType;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

import java.util.UUID;

public class UnblacklistCommand extends BaseCommand {
	
    public UnblacklistCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "unblacklist";
		this.permission = Permission.OP_PERMISSION;
	}
    
	@Override
	public void execute(CommandSender sender, String[] args) {     
        UUID senderUuid = null;
        String senderName = sender.getName();
        
        if(sender instanceof Player) {
            senderName = sender.getName();
		}
        
        if(args.length < 2) {
         	sender.sendMessage(Color.translate("&cUsage: /unblacklist <player> <reason>"));
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
        
        if(!profile.isBlacklisted()) {
            sender.sendMessage(Color.translate("&c" + ChatColor.stripColor(profile.getName()) + " &cis not blacklisted."));
            return;
        }
        
        for(Punishment punishment : profile.getPunishments()) {
            if(punishment.getType() == PunishmentType.BLACKLIST && punishment.isActive()) {
                if((!sender.hasPermission(Permission.OP_PERMISSION) && punishment.getAddedBy() == null) || (punishment.getAddedBy() != null && !punishment.getAddedBy().equals(senderUuid) && !sender.hasPermission(Permission.OP_PERMISSION))) {
                    sender.sendMessage(Color.translate("&cYou don't have permission to unblacklist a player you did not blacklist."));
                    return;
                }
                
                punishment.setRemovedAt(System.currentTimeMillis());
                punishment.setRemovedBy(senderName);
                punishment.setRemovedReason(reason);
                break;
            }
        }

        if(reason.contains("-s") || reason.contains("-silent")) {
            silent = true;
        }

        PunishPublisher.write("undo;BLACKLIST;" + VituzAPI.getServerName() + ";" + profile.getName() + ";" + senderName + ";" + reason + ";" + VituzAPI.getServerName() + ";" + silent);

        profile.save();
    }
}
