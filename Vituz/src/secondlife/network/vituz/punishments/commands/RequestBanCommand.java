package secondlife.network.vituz.punishments.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.ServerUtils;

public class RequestBanCommand extends BaseCommand {

    public RequestBanCommand(Vituz plugin) {
        super(plugin);

        this.command = "requestban";
        this.forPlayerUseOnly = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if(!VituzAPI.getRankName(player.getName()).equals("TrialMod")) {
            player.sendMessage(Color.translate("&cYou don't need to use this command!"));
            return;
        }

        if(args.length < 2) {
            player.sendMessage(Color.translate("&cUsage: /requestban <toBanPlayer> <reason and proof>"));
            return;
        }

        StringBuilder sb = new StringBuilder();

        for(int i = 1; i < args.length; ++i) {
            sb.append(args[i]).append(" ");
        }

        String reason = sb.toString().trim();
        ServerUtils.bungeeBroadcast("&4&l[Ban Request] &f" + player.getName() + " &chas requested ban for player &f" + args[0] + "! &c(Reason) &f" + reason + "&c!", Permission.STAFF_PERMISSION);
    }
}
