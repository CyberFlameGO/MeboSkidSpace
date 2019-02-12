package secondlife.network.overpass.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.overpass.data.OverpassData;
import secondlife.network.overpass.utilties.OverpassUtils;
import secondlife.network.vituz.utilties.Color;

import java.util.Arrays;

/**
 * Created by Marko on 11.05.2018.
 */
public class OverpassCommand extends Command {

    public OverpassCommand() {
        super("overpass");

        setAliases(Arrays.asList("authme"));
        setDescription("Overpass Command!");
        setPermission("secondlife.op");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!sender.hasPermission("secondlife.op")) return false;

        if(args.length < 2) {
            sender.sendMessage(Color.translate("&cUsage: /overpass <getpassword|getemail|resendmail|unregister|changeemail> <player>"));
        } else {
            if(args[0].equalsIgnoreCase("getpassword") || args[0].equalsIgnoreCase("checkpassword")) {
                Player target = Bukkit.getPlayer(args[1]);
                OverpassData overpassData;

                if(target == null) {
                    overpassData = OverpassData.getByName(args[1]);
                } else {
                    overpassData = OverpassData.getByName(target.getName());
                }

                if(!overpassData.isLoaded()) {
                    overpassData.load();
                }

                sender.sendMessage(Color.translate("&ePassword of player &d" + args[1] + " &eis &d" + overpassData.getPassword() + "&e."));
            } else if(args[0].equalsIgnoreCase("getemail") || args[0].equalsIgnoreCase("checkemail") || args[0].equalsIgnoreCase("getmail") || args[0].equalsIgnoreCase("mail")) {
                Player target = Bukkit.getPlayer(args[1]);
                OverpassData overpassData;

                if(target == null) {
                    overpassData = OverpassData.getByName(args[1]);
                } else {
                    overpassData = OverpassData.getByName(target.getName());
                }

                if(!overpassData.isLoaded()) {
                    overpassData.load();
                }

                sender.sendMessage(Color.translate("&eEmail of player &d" + args[1] + " &eis &d" + overpassData.getEmail() + "&e."));
            } else if(args[0].equalsIgnoreCase("resendmail")) {
                Player target = Bukkit.getPlayer(args[1]);
                OverpassData overpassData;

                if(target == null) {
                    overpassData = OverpassData.getByName(args[1]);
                } else {
                    overpassData = OverpassData.getByName(target.getName());
                }

                if(!overpassData.isLoaded()) {
                    overpassData.load();
                }

                OverpassCommands.sendEmail(target, overpassData.getEmail(), overpassData, overpassData.getCode());

                sender.sendMessage(Color.translate("&eEmail sent to &d" + overpassData.getEmail() + "&e."));
            } else if(args[0].equalsIgnoreCase("getcode") || args[0].equalsIgnoreCase("checkcode")) {
                Player target = Bukkit.getPlayer(args[1]);
                OverpassData overpassData;

                if(target == null) {
                    overpassData = OverpassData.getByName(args[1]);
                } else {
                    overpassData = OverpassData.getByName(target.getName());
                }

                if(!overpassData.isLoaded()) {
                    overpassData.load();
                }

                sender.sendMessage(Color.translate("&ePassword of player &d" + args[1] + " &eis &d" + overpassData.getCode()));
            } else if(args[0].equalsIgnoreCase("unregister")) {
                Player target = Bukkit.getPlayer(args[1]);
                OverpassData overpassData;

                if(target == null) {
                    overpassData = OverpassData.getByName(args[1]);
                } else {
                    overpassData = OverpassData.getByName(target.getName());
                }

                if(!overpassData.isLoaded()) {
                    overpassData.load();
                }

                overpassData.delete(sender);
            } else if(args[0].equalsIgnoreCase("changeemail")) {
                Player target = Bukkit.getPlayer(args[1]);
                OverpassData overpassData;

                if(target == null) {
                    overpassData = OverpassData.getByName(args[1]);
                } else {
                    overpassData = OverpassData.getByName(target.getName());
                }

                if(!overpassData.isLoaded()) {
                    overpassData.load();
                }

                String email = args[2];

                if(!(OverpassUtils.isValidEmailAddress(email))) {
                    sender.sendMessage(Color.translate("&cPlease specify a valid email address."));
                    return false;
                }

                if((email.contains("@"))
                        || (email.contains("hotmail"))
                        || (email.contains("outlook"))
                        || (email.contains("gmail"))
                        || (email.contains("net"))
                        || (email.contains("yahoo"))) {
                    String oldMail = overpassData.getEmail();

                    overpassData.setEmail(email);

                    if(overpassData.isNeedToEnterCode() && target != null) {
                        OverpassCommands.sendEmail(target, email, overpassData, overpassData.getCode());
                    }

                    if(target != null) {
                        target.kickPlayer(Color.translate("&cYour email has been changed!"));
                    } else {
                        overpassData.save();
                    }

                    sender.sendMessage(Color.translate("&eYou have successfully changed &d" + args[1] + " &eemail from &d" + oldMail + " &eto &d" + email));
                } else {
                    sender.sendMessage(Color.translate("&cPlease specify a valid email address."));
                }
            }
        }

        return false;
    }
}

