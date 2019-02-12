package secondlife.network.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import secondlife.network.bungee.Bungee;
import secondlife.network.bungee.utils.Color;

/**
 * Created by Marko on 09.04.2018.
 */
public class ReloadCommand extends Command {

    public ReloadCommand() {
        super("configreload", "secondlife.op", "cr", "creload", "configr");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Bungee.getInstance().reloadConfig();

        sender.sendMessage(Color.translate("&aYou have reloaded the configuration file!"));
    }
}