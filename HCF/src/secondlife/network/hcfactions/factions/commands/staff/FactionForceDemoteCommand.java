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

public class FactionForceDemoteCommand extends SubCommand {

	public FactionForceDemoteCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "forcedemote" };
		this.permission = Permission.OP_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        if(args.length < 2) {
            player.sendMessage(Color.translate("&cUsage: /f forcedemote <player>"));
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

        if(factionMember.getRole() != Role.LEADER) {
        	player.sendMessage(HCFUtils.INVALID_ROLE);
            return;
        }

        factionMember.setRole(Role.MEMBER);        
        playerFaction.broadcast("&d" + player.getName() + " &ehas been forcefully assigned as a member!");
    }
}
