package secondlife.network.vituz.punishments.commands;

import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.PunishData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class AltViewCommand extends BaseCommand {
	
    public AltViewCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "altview";
		this.permission = Permission.STAFF_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
        	sender.sendMessage(Color.translate("&cUsage: /altview <player>"));
            return;
        }

		PunishData profile = PunishData.getByName(args[0]);

		if (!profile.isLoaded()) {
			profile.load();
		}
        
        sender.sendMessage(Color.translate("&7Checking alternate accounts..."));

		new BukkitRunnable() {
			public void run() {
				if(profile.getAlts().isEmpty()) {
					Bukkit.getScheduler().runTask(Vituz.getInstance(), () -> sender.sendMessage(Color.translate("&cNo alternate accounts found.")));
				} else {
					int banned = 0;
					int muted = 0;
					int blackedlisted = 0;

					StringBuilder accounts = new StringBuilder();

					for(String uuids : profile.getAlts()) {
						PunishData profiles = PunishData.getByName(uuids);

						if(profiles.isBanned()) {
							++banned;
						}

						if(profiles.isMuted()) {
							++muted;
						}

						if(profiles.isBlacklisted()) {
							++blackedlisted;
						}
					
						ChatColor color = (profiles.isBlacklisted() ? ChatColor.DARK_RED : (profiles.isBanned() ? ChatColor.RED : (profiles.isMuted() ? ChatColor.GOLD : ((Bukkit.getPlayer(uuids) == null) ? ChatColor.GRAY : ChatColor.GREEN))));
						accounts.append(ChatColor.GRAY + ", " + color + ((profiles.getName() == null) ? "No-Name-Found" : profiles.getName()));
					}
                     
					accounts.append(".");
					Triple<Integer, Integer, Integer> finalNumbers = Triple.of(blackedlisted, banned, muted);
                     
					new BukkitRunnable() {
						public void run() {
							sender.sendMessage(Color.translate("&eThis account currently has &c" + profile.getAlts().size() + " &ealternate accounts."));
							sender.sendMessage(Color.translate("&eOf which &4" + finalNumbers.getLeft() + " &eare blacklisted, &c" + finalNumbers.getMiddle() + " &eare banned, and &f" + finalNumbers.getRight() + " &ehave other chat offenses."));
							sender.sendMessage(Color.translate("&a" + profile.getName() + " &ealternate accounts: &e" + accounts.toString()));
						}
					}.runTask(Vituz.getInstance());
				}
        	}
        }.runTaskAsynchronously(this.getPlugin());
	}
}
