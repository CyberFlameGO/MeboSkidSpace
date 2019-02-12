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
import secondlife.network.vituz.utilties.Msg;

public class FactionCoLeaderCommand extends SubCommand {

    public FactionCoLeaderCommand(HCF plugin) {
		super(plugin);
		
		this.aliases = new String[] { "coleader"  };
		this.forPlayerUseOnly = true;
    }

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        if(args.length < 2) {
            player.sendMessage(Color.translate("&cUsage: /f coleader <player>"));
            return;
        }
        
        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player.getName());
        
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
        
        if(Msg.checkOffline(player, args[1])) return;
        
        if(targetMember.getRole().equals(Role.COLEADER)) {
            player.sendMessage(Color.translate("&cThis member is already a co-leader!"));
            return;
        }
        
        if(targetMember.getName().equals(player.getName())) {
            player.sendMessage(Color.translate("&cYou are the leader, which means you cannot co-leader yourself."));
            return;
        }
        
        targetMember.setRole(Role.COLEADER);
        playerFaction.broadcast(Color.translate("&2" + targetMember.getName() + " &ehas been promoted to a co leader."));
    }
}
