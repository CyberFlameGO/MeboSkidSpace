package secondlife.network.hcfactions.factions.commands.captain;

import java.util.Set;

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

public class FactionUninviteCommand extends SubCommand {
    
    public FactionUninviteCommand(HCF plugin) {
		super(plugin);
		
		this.aliases = new String[] { "uninvite", "deinvite", "deinv", "uninv", "revoke" };
		this.forPlayerUseOnly = true;
    }

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        if(args.length < 2) {
            player.sendMessage(Color.translate("&cUsage: /f uninvite <all|player>"));
            return;
        }

        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

        if(playerFaction == null) {
            player.sendMessage(HCFUtils.NO_FACTION);
            return;
        }

        FactionMember factionMember = playerFaction.getMember(player);

        if(factionMember.getRole() == Role.MEMBER) {
            player.sendMessage(HCFUtils.INVALID_ROLE);
            return;
        }

        Set<String> invitedPlayerNames = playerFaction.getInvitedPlayerNames();

        if (args[1].equalsIgnoreCase("all")) {
            invitedPlayerNames.clear();
            player.sendMessage(Color.translate("&aYou have cleared all pending invitations!"));
            return;
        }

        if (!invitedPlayerNames.remove(args[1])) {
            player.sendMessage(Color.translate("&cThere is not a pending invitation for &l" + args[1] + "&c!"));
            return;
        }

        playerFaction.broadcast("&7" + factionMember.getRole().getAstrix() + "&2" + sender.getName() + " &ehas uninvited &d" + args[1] + " &efrom the faction.");
    }
}
