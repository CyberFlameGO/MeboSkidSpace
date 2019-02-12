package secondlife.network.hcfactions.factions.commands.captain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.vituz.utilties.Color;

public class FactionInvitesCommand extends SubCommand {

    public FactionInvitesCommand(HCF plugin) {
		super(plugin);
		
		this.aliases = new String[] { "invites" };
		this.forPlayerUseOnly = true;
    }

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        List<String> receivedInvites = new ArrayList<>();
        
        for(Faction faction : RegisterHandler.getInstancee().getFactionManager().getFactions()) {
            if(faction instanceof PlayerFaction) {
                PlayerFaction targetPlayerFaction = (PlayerFaction) faction;
                
                if(targetPlayerFaction.getInvitedPlayerNames().contains(sender.getName())) {
                    receivedInvites.add(targetPlayerFaction.getDisplayName(sender));
                }
            }
        }

        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction((Player) sender);
        String delimiter = Color.translate("&7, &d");

        if(playerFaction != null) {
            Set<String> sentInvites = playerFaction.getInvitedPlayerNames();
            
            player.sendMessage(Color.translate("&eSent by &d" + playerFaction.getDisplayName(sender) + "&7 (&d" + sentInvites.size() + "&7): &e" + (sentInvites.isEmpty() ? "Your faction has not invited anyone." : StringUtils.join(sentInvites, delimiter) + "!")));
        }

        player.sendMessage(Color.translate("&eRequested &7(&d" + receivedInvites.size() + "&7): &e" + (receivedInvites.isEmpty() ? "No factions have invited you." : StringUtils.join(receivedInvites, ChatColor.WHITE + delimiter) + "!")));
    }
}