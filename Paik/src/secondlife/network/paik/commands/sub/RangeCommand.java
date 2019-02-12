package secondlife.network.paik.commands.sub;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.commands.PaikCommand;
import secondlife.network.paik.utilties.command.Command;
import secondlife.network.paik.utilties.command.CommandArgs;

public class RangeCommand extends PaikCommand {

    private Paik plugin = Paik.getInstance();

    @Command(name = "setrangevl", aliases = { "range" }, permission = "secondlife.op")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if(args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /setrangevl <volume>");
            return;
        }

        try {
            final double volume = Double.parseDouble(args[0]);
            this.plugin.setRangeVl(volume);

            player.sendMessage(ChatColor.GREEN + "Range volume has been set to " + volume);
        }
        catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "That's not a correct value.");
        }
    }

}
