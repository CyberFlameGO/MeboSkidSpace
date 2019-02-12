package secondlife.network.paik.commands.sub;

import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.commands.PaikCommand;
import secondlife.network.paik.utilties.command.Command;
import secondlife.network.paik.utilties.command.CommandArgs;
import secondlife.network.vituz.utilties.Color;

public class PaikFunCommand extends PaikCommand {

    @Command(name = "paik", aliases = {"anticheat"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if(args.length == 0) {
            player.sendMessage(Color.translate("&eThis server is running &fPaik Anticheat&e made by &fItsNature &eand &fVISUAL_&e!"));
        }
    }

}
