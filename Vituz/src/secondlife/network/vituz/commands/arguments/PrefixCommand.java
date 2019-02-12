package secondlife.network.vituz.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;

/**
 * Created by Marko on 27.04.2018.
 */
public class PrefixCommand extends BaseCommand {

    public PrefixCommand(Vituz plugin) {
        super(plugin);

        this.command = "prefix";
        this.forPlayerUseOnly = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if(args.length == 0) {
            player.openInventory(plugin.getPrefixesManager().getInventory());
        }
    }
}
