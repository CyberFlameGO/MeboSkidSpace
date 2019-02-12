package secondlife.network.hcfactions.factions.commands.captain;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.ClaimableFaction;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.claim.ClaimZone;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;

public class FactionClaimsCommand extends SubCommand {

    public FactionClaimsCommand(HCF plugin) {
		super(plugin);
		
		this.aliases = new String[] { "claims" };
		this.forPlayerUseOnly = true;
    }

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		PlayerFaction selfFaction = sender instanceof Player ? RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player) : null;
        ClaimableFaction targetFaction;
        
        if(args.length < 2) {
            if(selfFaction == null) {
                player.sendMessage(HCFUtils.NO_FACTION);
                return;
            }

            targetFaction = selfFaction;
        } else {
            Faction faction = RegisterHandler.getInstancee().getFactionManager().getContainingFaction(args[1]);

            if(faction == null) {
            	player.sendMessage(HCFUtils.FACTION_NOT_FOUND);
                return;
            }

            if(!(faction instanceof ClaimableFaction)) {
                player.sendMessage(Color.translate("&cYou can only check the claims of factions that can have claims!"));
                return;
            }

            targetFaction = (ClaimableFaction) faction;
        }

        Collection<ClaimZone> claims = targetFaction.getClaims();

        if(claims.isEmpty()) {
            player.sendMessage(Color.translate("&cFaction &l" + targetFaction.getDisplayName(sender) + " &chas no claimed land!"));
            return;
        }

        if(sender instanceof Player && !sender.isOp() && (targetFaction instanceof PlayerFaction && ((PlayerFaction) targetFaction).getHome() == null)) {
            if(selfFaction != targetFaction) {
                player.sendMessage(Color.translate("&cYou cannot view the claims of &l" + targetFaction.getDisplayName(sender) + " &cbecause their home is unset!"));
                return;
            }
        }

        player.sendMessage(Color.translate("&eClaims of &d" + targetFaction.getDisplayName(sender)  + "&7 (&d" + claims.size() + "&7):"));

        for(ClaimZone claim : claims) {
            player.sendMessage(Color.translate("&7 " + claim.getFormattedName()));
        }

        return;
    }
}
