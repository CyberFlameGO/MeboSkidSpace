package secondlife.network.vituz.ranks.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.RankData;
import secondlife.network.vituz.managers.RankManager;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class GrantsCommand extends BaseCommand {
	
    public GrantsCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "grants";
		this.permission = Permission.OP_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if(args.length == 0) {
            player.sendMessage(Color.translate("&cUsage: /grants <player>"));
            return;
        }
        
        Player target = Bukkit.getPlayer(args[0]);

        RankData data;

        if(target == null) {
            data = RankData.getByName(args[0]);
        } else {
            data = RankData.getByName(target.getName());
        }

        if (!data.isLoaded()) {
            data.load();
        }

        player.sendMessage(Color.translate("&eDisplaying the grants of &d" + args[0] + "&e."));
        player.openInventory(plugin.getRankManager().getGrantsInventory(data, args[0], 1));
    }
}
