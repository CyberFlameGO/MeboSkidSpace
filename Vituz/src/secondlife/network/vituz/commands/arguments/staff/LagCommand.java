package secondlife.network.vituz.commands.arguments.staff;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class LagCommand extends BaseCommand {

	public LagCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "lag";
		this.permission = Permission.ADMIN_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		DecimalFormat dc = new DecimalFormat("##.##");
		double tps = Bukkit.spigot().getTPS()[0];
		
		long serverTime = ManagementFactory.getRuntimeMXBean().getStartTime();
		String uptime = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - serverTime, true, true);
		
		sender.sendMessage(Color.translate("&7&m----------------------"));
		sender.sendMessage(Color.translate("&e&lServer Info&7:"));
		sender.sendMessage(Color.translate(" &7* &eTPS&7: &d" + dc.format(tps)));
		sender.sendMessage(Color.translate(" &7* &eUptime&7: &d" + uptime));
		sender.sendMessage(Color.translate(" &7* &eMaximum Memory&7: &d" + Runtime.getRuntime().maxMemory() / 1024 / 1024 + " &eMB"));
		sender.sendMessage(Color.translate(" &7* &eAllocated Memory&7: &d" + Runtime.getRuntime().totalMemory() / 1024 / 1024 + " &eMB"));
		sender.sendMessage(Color.translate(" &7* &eFree Memory&7: &d" + Runtime.getRuntime().freeMemory() / 1024 / 1024 + " &eMB"));
		sender.sendMessage("");
		sender.sendMessage(Color.translate("&e&lWorlds&7:"));
		
		for(World world : Bukkit.getWorlds()) {
			sender.sendMessage(Color.translate(" &7* &d" + world.getName() + "&7: &eLoaded Chunks&7: &d" + world.getLoadedChunks().length + "&7, &eEntities&7: &d" + world.getEntities().size()));
		}
		
		sender.sendMessage(Color.translate("&7&m----------------------"));
	}
}