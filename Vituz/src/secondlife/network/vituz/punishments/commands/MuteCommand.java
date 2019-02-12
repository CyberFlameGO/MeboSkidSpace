package secondlife.network.vituz.punishments.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.PunishData;
import secondlife.network.vituz.punishments.redis.PunishPublisher;
import secondlife.network.vituz.punishments.PunishmentQueue;
import secondlife.network.vituz.punishments.PunishmentType;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.DateUtil;
import secondlife.network.vituz.utilties.Permission;

public class MuteCommand extends BaseCommand {
	
    public MuteCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "mute";
		this.permission = Permission.STAFF_PERMISSION;
	}
    
	@Override
	public void execute(CommandSender sender, String[] args) {     
        String senderName = sender.getName();
        
        if(sender instanceof Player) {
            senderName = sender.getName();
		}

        if(args.length < 3) {
            sender.sendMessage(Color.translate("&cUsage: /mute <player> <duration> <reason>"));
            sender.sendMessage(Color.translate("&cExample: /mute ItsNature 2h Spam"));
            sender.sendMessage(Color.translate("&cFor permanent use 'perm' or 'permanent' as duration!"));
            return;
        }

        PunishData profile = PunishData.getByName(args[0]);

        if(!profile.isLoaded()) {
            profile.load();
        }

        // REASON
        StringBuilder sb = new StringBuilder();

        for(int i = 2; i < args.length; ++i) {
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

        // DURATION
        long duration;

        if(args[1].equalsIgnoreCase("perm") || args[1].equalsIgnoreCase("permanent")) {
            duration = 2147483647L;
        } else {
            try {
                duration = System.currentTimeMillis() - DateUtil.parseDateDiff(args[1], false);
            } catch(Exception e) {
                sender.sendMessage(Color.translate("&cInvalid duration."));
                return;
            }
        }

        if(!VituzAPI.canPunish(sender, args[0])) {
            sender.sendMessage(Color.translate("&cSorry but you can't punish " + args[0] + "!"));
            return;
        }

        if(profile.isMuted()) {
            sender.sendMessage(Color.translate("&c" + ChatColor.stripColor(profile.getName()) + " &cis already muted."));
            return;
        }

        if(duration == 2147483647L) {
            new PunishmentQueue(profile.getName(), PunishmentType.MUTE);
            PunishPublisher.write("punishment;MUTE;" + VituzAPI.getServerName() + ";" + profile.getName() + ";" + senderName + ";" + reason + ";" + silent + ";" + VituzAPI.getServerName());
        } else {
            new PunishmentQueue(profile.getName(), PunishmentType.MUTE);
            PunishPublisher.write("punishment;MUTE;" + VituzAPI.getServerName() + ";" + profile.getName() + ";" + senderName + ";" + reason + ";" + silent + ";" + VituzAPI.getServerName() + ";" + duration);
        }
    }
}
