package secondlife.network.uhc.commands.arguments;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.command.CommandSender;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.commands.BaseCommand;
import secondlife.network.uhc.tasks.AutoStartTask;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.StringUtils;

public class AutoStartCommand extends BaseCommand {

    public AutoStartCommand(UHC plugin) {
        super(plugin);

        this.command = "autostart";
        this.permission = Permission.OP_PERMISSION;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(Color.translate("&cUsage: /autostart <time|cancel>"));
        } else {
            if(args[0].equalsIgnoreCase("cancel")) {
                if(AutoStartTask.running) {
                    AutoStartTask.starting.cancelRunnable();
                } else {
                    sender.sendMessage(Color.translate("&cGame is not starting."));
                }
            } else {
                if(AutoStartTask.running) {
                    sender.sendMessage(Color.translate("&cGame is already starting."));
                    return;
                }

                long duration = StringUtils.parse(args[0]);

                if(duration == -1) {
                    sender.sendMessage(Color.translate("&cInvalid duration."));
                    return;
                }

                sender.sendMessage(Color.translate("&eYou have set autostart time to &d" + DurationFormatUtils.formatDurationWords(duration, true, true) + "&e."));

                new AutoStartTask(duration);
            }
        }
    }
}