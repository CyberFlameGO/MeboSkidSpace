package secondlife.network.hcfactions.factions.commands.member;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.FactionMember;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.factions.claim.ClaimZone;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;

public class FactionUnclaimCommand extends SubCommand {

	public FactionUnclaimCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "unclaim" };
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

        if(playerFaction == null) {
            sender.sendMessage(HCFUtils.NO_FACTION);
            return;
        }

        FactionMember factionMember = playerFaction.getMember(player);

        if(factionMember.getRole() != Role.LEADER) {
            sender.sendMessage(HCFUtils.INVALID_ROLE);
            return;
        }

        Collection<ClaimZone> factionClaims = playerFaction.getClaims();

        if(factionClaims.isEmpty()) {
            sender.sendMessage(Color.translate("&cYour faction don't have any claims."));
            return;
        }

        Collection<ClaimZone> removingClaims;
        
        if(args.length > 1 && args[1].equalsIgnoreCase("all")) {
            removingClaims = new ArrayList<>(factionClaims);
        } else {
            Location location = player.getLocation();
            ClaimZone claimAt = RegisterHandler.getInstancee().getFactionManager().getClaimAt(location);
            
            if(claimAt == null || !factionClaims.contains(claimAt)) {
                sender.sendMessage(Color.translate("&cYour faction don't have any claims."));
                return;
            }

            removingClaims = Collections.singleton(claimAt);
        }

        if(!playerFaction.removeClaims(removingClaims, player)) {
            sender.sendMessage(Color.translate("&c&lError when removing claims, please contact an Administrator."));
            return;
        }

        int removingAmount = removingClaims.size();
        playerFaction.broadcast("&7" + factionMember.getRole().getAstrix() + "&2" + sender.getName() + " &ehas removed &c&l" + removingAmount + " &eclaim" + (removingAmount > 1 ? "&es" : "") + "!");
    }
}
