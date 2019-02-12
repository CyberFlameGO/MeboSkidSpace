package secondlife.network.uhc.deathlookup;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.commands.BaseCommand;
import secondlife.network.uhc.deathlookup.data.DeathData;
import secondlife.network.vituz.utilties.Permission;

public class DeathLookupCommand extends BaseCommand {

    public DeathLookupCommand(UHC plugin) {
        super(plugin);

        this.command = "deathlookup";
        this.permission = Permission.STAFF_PERMISSION;
        this.forPlayerUseOnly = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        DeathData deathData = DeathData.getByName(player.getName());

        if(args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /deathlookup <target>");
            return;
        }

        DeathData toLookupDeathData;
        Player toLookup = Bukkit.getPlayer(StringUtils.join(args));
        if(toLookup != null) {
            toLookupDeathData = DeathData.getByName(toLookup.getName());
        } else {
            toLookupDeathData = DeathData.getByName(StringUtils.join(args));
        }

        if(toLookupDeathData == null) {
            player.sendMessage(ChatColor.RED + "No player with name '" + StringUtils.join(args) + "' found.");
            return;
        }

        deathData.setDeathLookup(new DeathLookup(toLookupDeathData));
        player.openInventory(deathData.getDeathLookup().getDeathInventory(1));
    }
}
