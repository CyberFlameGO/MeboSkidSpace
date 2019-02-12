package secondlife.network.hcfactions.factions.commands.captain;

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

public class FactionSetHomeCommand extends SubCommand {

    public FactionSetHomeCommand(HCF plugin) {
		super(plugin);
		
		this.aliases = new String[] { "sethome", "sethq" };
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

        if(factionMember.getRole() == Role.MEMBER) {
            player.sendMessage(HCFUtils.INVALID_ROLE);
            return;
        }

        Location location = player.getLocation();

        boolean insideTerritory = false;
        for(ClaimZone claim : playerFaction.getClaims()) {
            if(claim.contains(location)) {
                insideTerritory = true;
                break;
            }
        }

        if(!insideTerritory) {
            player.sendMessage(Color.translate("&cYou may only set your home in your territory!"));
            return;
        }

        String home = secondlife.network.vituz.utilties.StringUtils.stringifyLocation(location);

        playerFaction.setHome(home);
        playerFaction.broadcast("&2" + factionMember.getRole().getAstrix() + sender.getName() + " &ehas updated the faction home.");
        return;
    }

}
