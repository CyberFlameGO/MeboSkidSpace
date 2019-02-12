package secondlife.network.vituz.commands.arguments;

import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.ChallengeData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marko on 27.04.2018.
 */
public class ChallengeCommand extends BaseCommand {

    public ChallengeCommand(Vituz plugin) {
        super(plugin);

        this.command = "challenge";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            sendUsage(sender);
            return;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("gui") || args[0].equalsIgnoreCase("open")) {
                if(sender instanceof Player) {
                    Player player = (Player) sender;

                    player.openInventory(plugin.getChallengesManager().getInventory(player));
                    return;
                }

                sender.sendMessage(Msg.NO_CONSOLE);
            } else if(args[0].equalsIgnoreCase("top")) {
                getTop10(sender);
            } else {
                sendUsage(sender);
            }
        } else if(args.length == 2) {
            if (args[0].equalsIgnoreCase("points")) {
                Player target = Bukkit.getPlayer(args[1]);
                ChallengeData data;

                if(target == null) {
                    data = ChallengeData.getByName(args[1]);
                } else {
                    data = ChallengeData.getByName(target.getName());
                }

                sender.sendMessage(Color.translate("&e" + args[1] + " &ehas &c" + data.getPoints() + " &epoints."));
            } else {
                sendUsage(sender);
            }
        } else if(args.length == 3) {
            if (args[0].equalsIgnoreCase("setpoints")) {
                if (!sender.hasPermission(Permission.OP_PERMISSION)) {
                    sender.sendMessage(Msg.NO_PERMISSION);
                    return;
                }

                Player target = Bukkit.getPlayer(args[1]);
                ChallengeData data;

                if(target == null) {
                    data = ChallengeData.getByName(args[1]);
                } else {
                    data = ChallengeData.getByName(target.getName());
                }

                int amount = Integer.parseInt(args[2]);

                if(amount < 0 || amount > 1000) {
                    sender.sendMessage(Color.translate("&cInvalid amount"));
                    return;
                }

                sender.sendMessage(Color.translate("&cYou have set &4" + args[1] + " &cpoints to &4" + amount + " &cfrom &4" + data.getPoints()));
                if(target != null) target.sendMessage(Color.translate("&cYour points have been set by &4" + sender.getName() + " &cto &4" + amount + " &cfrom &4" + data.getPoints()));
                Msg.logConsole("[!] " + sender.getName() + " has set " + args[1] + " points to " + amount + " from " + data.getPoints());

                data.setPoints(amount);
            } else if (args[0].equalsIgnoreCase("givepoints")) {
                if (!sender.hasPermission(Permission.OP_PERMISSION)) {
                    sender.sendMessage(Msg.NO_PERMISSION);
                    return;
                }

                Player target = Bukkit.getPlayer(args[1]);
                ChallengeData data;

                if(target == null) {
                    data = ChallengeData.getByName(args[1]);
                } else {
                    data = ChallengeData.getByName(target.getName());
                }

                int amount = Integer.parseInt(args[2]);

                if(amount < 0 || amount > 1000) {
                    sender.sendMessage(Color.translate("&cInvalid amount"));
                    return;
                }

                sender.sendMessage(Color.translate("&cYou have given &4" + amount + " &cpoints to &4" + args[1]));
                if(target != null) target.sendMessage(Color.translate("&cYou have been given &4" + amount + " &cpoints by &4" + sender.getName()));
                Msg.logConsole("[!] " + sender.getName() + " has given " + amount + " points to " + args[1]);

                data.setPoints(data.getPoints() + amount);
            } else {
                sendUsage(sender);
            }
        } else {
            sendUsage(sender);
        }
    }

    public static void getTop10(CommandSender sender) {
        new BukkitRunnable() {
            public void run() {
                List<Document> documents = (List<Document>) Vituz.getInstance().getDatabaseManager().getChallengeData().find().limit(10).sort(new BasicDBObject("points", (-1))).into(new ArrayList());
                sender.sendMessage(Color.translate("&eListing 10 players with most points."));
                int index = 1;
                for (Document document : documents) {
                    String name = document.getString("realName");
                    if(name == null) name = document.getString("name");
                    int points = document.getInteger("points").intValue();

                    sender.sendMessage(Color.translate("&e#" + index++ + ": &d" + name + ": &e(" + points + ")"));
                }
            }
        }.runTaskAsynchronously(Vituz.getInstance());
    }

    public static void sendUsage(CommandSender sender) {
        sender.sendMessage(Color.translate("&c/challenge gui"));
        sender.sendMessage(Color.translate("&c/challenge top"));
        sender.sendMessage(Color.translate("&c/challenge points <player>"));

        if(sender.hasPermission(Permission.OP_PERMISSION)) {
            sender.sendMessage(Color.translate("&c/challenge setpoints <player> <points>"));
            sender.sendMessage(Color.translate("&c/challenge givepoints <player> <points>"));
        }
    }
}
