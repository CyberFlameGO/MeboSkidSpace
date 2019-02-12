package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.NumberUtils;
import secondlife.network.vituz.utilties.Permission;

/**
 * Created by Marko on 14.04.2018.
 */
public class TasksCommand extends BaseCommand {

    public TasksCommand(Vituz plugin) {
        super(plugin);

        this.command = "tasks";
        this.permission = Permission.OP_PERMISSION;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(Color.translate("&cUsage: /tasks <tab|scoreboard|nametags> <update>"));
        } else {
            if(args[0].equalsIgnoreCase("tab")) {
                int i = Integer.parseInt(args[1]);

                if(!NumberUtils.isInteger(args[1])) {
                    sender.sendMessage(Color.translate("&cInvalid number."));
                    return;
                }

                if(i > 99999999) {
                    sender.sendMessage(Color.translate("&cYou broke the limits lol"));
                    return;
                }

                VituzAPI.tabTime = i;

                sender.sendMessage(ChatColor.GREEN + "Changed tab update interval to " + i);
            } else if(args[0].equalsIgnoreCase("scoreboard")) {
                int i = Integer.parseInt(args[1]);

                if(!NumberUtils.isInteger(args[1])) {
                    sender.sendMessage(Color.translate("&cInvalid number."));
                    return;
                }

                if(i > 99999999) {
                    sender.sendMessage(Color.translate("&cYou broke the limits lol"));
                    return;
                }

                VituzAPI.scoreboardTime = i;

                sender.sendMessage(ChatColor.GREEN + "Changed scoreboard update interval to " + i);
            } else if(args[0].equalsIgnoreCase("nametags")) {
                int i = Integer.parseInt(args[1]);

                if(!NumberUtils.isInteger(args[1])) {
                    sender.sendMessage(Color.translate("&cInvalid number."));
                    return;
                }

                if(i > 99999999) {
                    sender.sendMessage(Color.translate("&cYou broke the limits lol"));
                    return;
                }

                VituzAPI.nametagsTime = i;

                sender.sendMessage(ChatColor.GREEN + "Changed scoreboard update interval to " + i);
            }
        }
    }
}
