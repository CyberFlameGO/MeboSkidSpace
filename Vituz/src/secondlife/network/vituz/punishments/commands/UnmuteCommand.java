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

public class UnmuteCommand extends BaseCommand {
	
    public UnmuteCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "unmute";
		this.permission = Permission.STAFF_PLUS_PERMISSION;
	}
    
	@Override
	public void execute(CommandSender sender, String[] args) {     
        String senderName = sender.getName();
        
        if(sender instanceof Player) {
            senderName = sender.getName();
		}
        
        if(args.length < 2) {
         	sender.sendMessage(Color.translate("&cUsage: /unmute <player> <reason>"));
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

        if(!profile.isMuted()) {
            sender.sendMessage(Color.translate("&c" + ChatColor.stripColor(profile.getName()) + " &cis not muted."));
            return;
        }
        
        for(Punishment punishment : profile.getPunishments()) {
            if(punishment.getType() == PunishmentType.MUTE && punishment.isActive()) {
                punishment.setRemovedAt(System.currentTimeMillis());
                punishment.setRemovedBy(senderName);
                punishment.setRemovedReason(reason);
                break;
            }
        }

        if(reason.contains("-s") || reason.contains("-silent")) {
            silent = true;
        }

        PunishPublisher.write("undo;MUTE;" + VituzAPI.getServerName() + ";" + profile.getName() + ";" + senderName + ";" + reason + ";" + VituzAPI.getServerName() + ";" + silent);

        profile.save();
    }
}
