package secondlife.network.paik.commands.sub;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.PaikAPI;
import secondlife.network.paik.handlers.PlayerHandler;
import secondlife.network.paik.handlers.LogsHandler;
import secondlife.network.paik.utilties.CustomLocation;
import secondlife.network.paik.utilties.events.player.PlayerBanEvent;
import secondlife.network.paik.utilties.command.CommandArgs;
import secondlife.network.paik.commands.PaikCommand;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.command.Command;
import secondlife.network.vituz.utilties.Msg;

import java.io.IOException;
import java.text.DecimalFormat;

public class PaikBanCommand extends PaikCommand {

    private Paik plugin = Paik.getInstance();

    @Command(name = "paikban", aliases = { "pban", "acban", "anticheatban" }, permission = "secondlife.op")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if(args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /paikban <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(Msg.checkOffline(player, args[0])) return;

        PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(target);
        playerData.setBanning(false);

        PlayerBanEvent event = new PlayerBanEvent(target, ChatColor.AQUA + "was banned by " + ChatColor.RED + player.getName() + ".");
        this.plugin.getServer().getPluginManager().callEvent(event);

        try {
            LogsHandler.log(target, "", "BANNED WITH /PAIKBAN COMMAND BY " + player.getName(), CustomLocation.getLocation(target), PaikAPI.getPing(target), new DecimalFormat("##.##").format(Bukkit.spigot().getTPS()[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
