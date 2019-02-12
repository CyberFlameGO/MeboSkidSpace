package secondlife.network.vituz.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.managers.SettingsManager;

/**
 * Created by Marko on 21.04.2018.
 */
public class SettingsCommand extends BaseCommand {

    public SettingsCommand(Vituz plugin) {
        super(plugin);

        this.command = "settings";
        this.forPlayerUseOnly = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        new SettingsManager(player).openInventory(player);
    }
}
