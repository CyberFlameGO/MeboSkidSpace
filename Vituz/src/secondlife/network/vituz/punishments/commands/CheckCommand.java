package secondlife.network.vituz.punishments.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.PunishData;
import secondlife.network.vituz.punishments.Punishment;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class CheckCommand extends BaseCommand {
	
    public CheckCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "checkpunishments";
		this.forPlayerUseOnly = true;
		this.permission = Permission.STAFF_PERMISSION;
	}
    
	@Override
	public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if(args.length == 0) {
            player.sendMessage(Color.translate("&cUsage: /checkpunishments <player>"));
            return;
        }
        
        Player target = Bukkit.getPlayer(args[0]);

        PunishData profile;

        if(target == null) {
            profile = PunishData.getByName(args[0]);
        } else {
            profile = PunishData.getByName(target.getName());
        }

        if (!profile.isLoaded()) {
            profile.load();
        }

        player.openInventory(Punishment.getMenu(profile));
    }
}
