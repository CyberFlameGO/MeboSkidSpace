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

public class FactionForceLeaderCommand extends SubCommand {

	public FactionForceLeaderCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "forceleader" };
		this.permission = Permission.OP_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        if(args.length < 2) {
            player.sendMessage(Color.translate("&cUsage: /f forceleader <player>"));
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

        if(factionMember.getRole() == Role.LEADER) {
        	player.sendMessage(HCFUtils.INVALID_ROLE);
            return;
        }

        FactionMember leader = playerFaction.getLeader();
        
        String oldLeaderName = leader == null ? "none" : leader.getName();
        String newLeaderName = factionMember.getName();

        if(leader != null) {
            leader.setRole(Role.CAPTAIN);
        }

        factionMember.setRole(Role.LEADER);
        
        playerFaction.broadcast("&d" + sender.getName() + " &ehas forcefully set the leader to &d" + newLeaderName + "!");

        player.sendMessage(Color.translate("&eLeader of &d" + playerFaction.getName() + " &ewas forcefully set from &d" + oldLeaderName + " &eto &d" + newLeaderName));
    }
}
