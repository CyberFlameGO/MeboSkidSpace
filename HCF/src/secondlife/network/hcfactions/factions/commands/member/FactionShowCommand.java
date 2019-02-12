package secondlife.network.hcfactions.factions.commands.member;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;

public class FactionShowCommand extends SubCommand {

	public FactionShowCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "show", "who" };
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
        Faction playerFaction = null;
        Faction namedFaction;

        if(args.length < 2) {
            namedFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction((Player) sender);

            if(namedFaction == null) {
                sender.sendMessage(HCFUtils.NO_FACTION);
                return;
            }
        } else {
            namedFaction = RegisterHandler.getInstancee().getFactionManager().getFaction(args[1]);
            playerFaction = RegisterHandler.getInstancee().getFactionManager().getContainingPlayerFaction(args[1]);

            if(namedFaction == null && playerFaction == null) {
            	sender.sendMessage(HCFUtils.FACTION_NOT_FOUND);
                return;
            }
        }

        if(namedFaction != null) {
            namedFaction.printDetails(sender);
        }

        if(playerFaction != null && namedFaction != playerFaction) {
            playerFaction.printDetails(sender);
        }
    }
}
