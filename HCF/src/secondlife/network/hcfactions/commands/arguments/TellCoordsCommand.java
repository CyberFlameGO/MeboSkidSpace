package secondlife.network.hcfactions.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;

public class TellCoordsCommand extends BaseCommand {

	public TellCoordsCommand(HCF plugin) {
		super(plugin);
		
		this.command = "tellcoords";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
 		
		if(args.length == 0) {
			PlayerFaction faction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);
			
			if(faction == null) {
				player.sendMessage(HCFUtils.NO_FACTION);
				return;
			}
			
			faction.broadcast("&3(Faction) " + player.getName() + ": &b&lX&7: &d" + (int) player.getLocation().getX() + "&7, &b&lY&7: &d" + (int) player.getLocation().getY() + "&7, &b&lZ&7: &d" + (int) player.getLocation().getZ() + "&3!");
		}
	}

}
