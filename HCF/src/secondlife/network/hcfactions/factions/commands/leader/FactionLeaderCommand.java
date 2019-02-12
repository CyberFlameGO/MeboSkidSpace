package secondlife.network.hcfactions.factions.commands.leader;

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

public class FactionLeaderCommand extends SubCommand {

	public FactionLeaderCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "leader" };
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        if(args.length < 2) {
            player.sendMessage(Color.translate("&cUsage: /f leader <player>"));
            return;
        }

        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

        if(playerFaction == null) {
            player.sendMessage(HCFUtils.NO_FACTION);
            return;
        }

        FactionMember selfMember = playerFaction.getMember(player.getName());
        
        Role selfRole = selfMember.getRole();
        if(selfRole != Role.LEADER) {
        	player.sendMessage(HCFUtils.INVALID_ROLE);
            return;
        }

        FactionMember targetMember = playerFaction.getMember(args[1]);

        if(targetMember == null) {
        	player.sendMessage(Color.translate("&cThat player is not in your faction."));
            return;
        }

        if(targetMember.getName().equals(player.getName())) {
            player.sendMessage(Color.translate("&cYou are already the faction leader."));
            return;
        }

        targetMember.setRole(Role.LEADER);
        selfMember.setRole(Role.CAPTAIN);
        
        playerFaction.broadcast("&2" + selfMember.getRole().getAstrix() + selfMember.getName() + " &ehas transferred the faction to &2"  + targetMember.getRole().getAstrix() + targetMember.getName());
    }
}
