package secondlife.network.vituz.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

/**
 * Created by Marko on 09.05.2018.
 */
public class PingCommand extends BaseCommand {

    public PingCommand(Vituz plugin) {
        super(plugin);

        this.command = "ping";
        this.forPlayerUseOnly = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if(args.length == 0) {
            player.sendMessage(Color.translate("&eYour ping is &d" + VituzAPI.getPing(player) + " ms&e."));
        } else {
            Player target = Bukkit.getPlayer(args[0]);

            if(Msg.checkOffline(sender, args[0])) return;

            player.sendMessage(Color.translate("&ePing of player &d" + target.getName() + " &eis &d" + VituzAPI.getPing(target) + " ms&e."));
            player.sendMessage(Color.translate("&ePing difference: &d" + (Math.max(VituzAPI.getPing(player), VituzAPI.getPing(target)) - Math.min(VituzAPI.getPing(player), VituzAPI.getPing(target)) + " &ems") + "&e."));
        }
    }
}
