package secondlife.network.hub.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hub.managers.QueueManager;
import secondlife.network.hub.data.QueueData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.command.Command;
import secondlife.network.vituz.utilties.command.param.Parameter;

/**
 * Created by Marko on 22.07.2018.
 */
public class QueueCommands {

    @Command(names = {"leavequeue", "lq"})
    public static void handleUsage(Player player) {
        if(QueueManager.getByPlayer(player) == null) {
            player.sendMessage(Color.translate("&cYou aren't in the queue!"));
            return;
        }

        player.sendMessage(Color.translate("&dYou are no longer in queue for &f" + QueueManager.getQueueName(player) + "&d."));

        QueueManager.getByPlayer(player).handleRemove(player);
    }

    @Command(names = {"limitqueue", "queuelimit"}, permissionNode = "secondlife.op")
    public static void handleSellwand(CommandSender sender, @Parameter(name = "name") String server, @Parameter(name = "amount") int amount) {
        if(QueueManager.getByServer(server) == null) {
            sender.sendMessage(Color.translate("&cQueue named " + server + " doesn't exists!"));
            return;
        }

        QueueData queue = QueueManager.getByServer(server);
        queue.setLimit(amount);

        sender.sendMessage(Color.translate("&f" + queue.getServer() + " &dlimit set to &f" + amount + "&d."));
    }

    @Command(names = {"pausequeue"})
    public static void handleUsage(CommandSender sender, @Parameter(name = "name") String server) {
        if(QueueManager.getByServer(server) == null) {
            sender.sendMessage(Color.translate("&cQueue named " + server + " doesn't exists!"));
            return;
        }

        QueueData queue = QueueManager.getByServer(server);
        queue.setPaused(!queue.isPaused());
        sender.sendMessage(Color.translate("&f" + queue.getServer() + " &dqueue has been " + (queue.isPaused() ? "&apaused" : "&cunpaused") + "&d."));
    }
}
