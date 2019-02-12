package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.command.CommandSender;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.status.ServerData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

import java.text.DecimalFormat;

public class StatusCommand extends BaseCommand {

    private DecimalFormat dc = new DecimalFormat("##.##");

    public StatusCommand(Vituz plugin) {
        super(plugin);

        this.command = "status";
        this.permission = Permission.OP_PERMISSION;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(Color.translate("&cUsage: /status <server|all>"));
        } else {
            if(args[0].equalsIgnoreCase("all")) {
                for(ServerData data : ServerData.getServers()) {
                    sender.sendMessage(Color.translate("&d" + data.getName() + "&e: Count &d(" + data.getOnlinePlayers() + "/" + data.getMaximumPlayers() + ") &eStatus &d(" + data.getTranslatedStatus() + "&d) &eTPS &d(" + dc.format(data.getTps()) + ")"));
                }
            } else {
                String server = args[0];

                ServerData data = ServerData.getByName(server);

                if(data != null) {
                    sender.sendMessage(Msg.BIG_LINE);
                    sender.sendMessage("");
                    sender.sendMessage(Color.translate("&eServer status of &d" + VituzAPI.getServerData(server).getName() + "&e:"));
                    sender.sendMessage(Color.translate("&eStatus: &d" + VituzAPI.getServerData(server).getTranslatedStatus()));
                    sender.sendMessage(Color.translate("&eOnline: &d" + VituzAPI.getServerData(server).getOnlinePlayers() + "/" + VituzAPI.getServerData(server).getMaximumPlayers()));
                    sender.sendMessage(Color.translate("&eMotd: &d" + VituzAPI.getServerData(server).getMotd()));
                    sender.sendMessage(Color.translate("&eTPS: &d" + dc.format(VituzAPI.getServerData(server).getTps())));
                    sender.sendMessage("");
                    sender.sendMessage(Msg.BIG_LINE);
                }
            }
        }
    }
}
