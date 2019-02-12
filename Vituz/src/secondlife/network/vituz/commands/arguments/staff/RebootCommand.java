package secondlife.network.vituz.commands.arguments.staff;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.events.RebootStartEvent;
import secondlife.network.vituz.events.RebootStopEvent;
import secondlife.network.vituz.tasks.RebootTask;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.StringUtils;

public class RebootCommand extends BaseCommand {

	public RebootCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "reboot";
		this.permission = Permission.OP_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /reboot <time|cancel>"));
		} else {
			if(args[0].equalsIgnoreCase("cancel")) {
				if(RebootTask.running) {
					RebootTask.reboot.cancelRunnable();
					Bukkit.getPluginManager().callEvent(new RebootStopEvent());
				} else {
					sender.sendMessage(Color.translate("&cServer is currently not rebooting."));
				}
			} else {
				if(RebootTask.running) {
					sender.sendMessage(Color.translate("&cServer is already rebooting."));
					return;
				}
				
				long duration = StringUtils.parse(args[0]);
				
				if(duration == -1) {
					sender.sendMessage(Color.translate("&cInvalid duration."));
					return;
				}
				
				sender.sendMessage(Color.translate("&eYou have set reboot time to &d" + DurationFormatUtils.formatDurationWords(duration, true, true) + "&e."));
				
				new RebootTask(duration);
				Bukkit.getPluginManager().callEvent(new RebootStartEvent());
			}
		}
		
	}
}
