package secondlife.network.hcfactions.factions.commands.staff;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.FactionMember;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class FactionForcePromoteCommand extends SubCommand {

	public FactionForcePromoteCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "forcepromote" };
		this.permission = Permission.OP_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        if(args.length < 2) {
            player.sendMessage(Color.translate("&cUsage: /f forcepromote <player>"));
            return;
        }

        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getContainingPlayerFaction(args[1]);

        if(playerFaction == null) {
        	player.sendMessage(HCFUtils.FACTION_NOT_FOUND);
            return;
        }

        FactionMember factionMember = playerFaction.getMember(args[1]);

        if(factionMember == null) {
        	player.sendMessage(HCFUtils.FACTION_NOT_FOUND);
            return;
        }

        if(factionMember.getRole() != Role.MEMBER) {
        	player.sendMessage(HCFUtils.INVALID_ROLE);
            return;
        }

        factionMember.setRole(Role.CAPTAIN);
        
        playerFaction.broadcast("&d" + player.getName() + " &ehas been forcefully assigned as a captain!");
    }
}