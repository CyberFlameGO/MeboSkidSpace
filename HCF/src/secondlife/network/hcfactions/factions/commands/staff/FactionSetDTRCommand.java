package secondlife.network.hcfactions.factions.commands.staff;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.utilties.JavaUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class FactionSetDTRCommand extends SubCommand {

	public FactionSetDTRCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "setdtr", "dtr" };
		this.permission = Permission.STAFF_PLUS_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        if(args.length < 3) {
            player.sendMessage(Color.translate("Correct Usage: /f setdtr <playerName|factionName> <newDTR>."));
            return;
        }

        Double newDTR = JavaUtils.tryParseDouble(args[2]);

        if(newDTR == null) {
        	player.sendMessage(Color.translate("&cInvalid Number."));
            return;
        }

        if(args[1].equalsIgnoreCase("all")) {
            for(Faction faction : RegisterHandler.getInstancee().getFactionManager().getFactions()) {
                if(faction instanceof PlayerFaction) {
                    ((PlayerFaction) faction).setDeathsUntilRaidable(newDTR);
                }
            }

            Command.broadcastCommandMessage(sender, Color.translate("&eSet DTR of all factions to &d" + newDTR));
            return;
        }

        Faction faction = RegisterHandler.getInstancee().getFactionManager().getContainingFaction(args[1]);

        if(faction == null) {
        	player.sendMessage(HCFUtils.FACTION_NOT_FOUND);
            return;
        }

        if(!(faction instanceof PlayerFaction)) {
            player.sendMessage(Color.translate("&cYou can only set DTR of player factions!"));
            return;
        }

        PlayerFaction playerFaction = (PlayerFaction) faction;
        
        double previousDtr = playerFaction.getDeathsUntilRaidable();
        newDTR = playerFaction.setDeathsUntilRaidable(newDTR);

        Command.broadcastCommandMessage(sender, Color.translate("&eSet DTR of &d" + faction.getName() + " &efrom &d" + previousDtr + " &eto &d" + newDTR));
    }
}
