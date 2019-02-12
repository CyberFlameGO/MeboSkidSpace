package secondlife.network.hcfactions.handlers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.commands.arguments.FocusCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.timers.ArcherHandler;
import secondlife.network.vituz.providers.NametagProvider;
import secondlife.network.vituz.providers.nametags.NametagInfo;

/**
 * Created by Marko on 01.04.2018.
 */
public class NametagsHandler extends NametagProvider {

    public NametagsHandler(String name, int weight) {
        super(name, weight);
    }

    @Override
    public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
        PlayerFaction faction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(toRefresh);
        PlayerFaction targetFac = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(refreshFor);

        if(FocusCommand.focus.containsKey(targetFac)) {
            return createNametag(ChatColor.AQUA + ChatColor.BOLD.toString(), "");
        }

        if(ArcherHandler.tag.contains(toRefresh.getUniqueId())) {
            return createNametag(ChatColor.DARK_RED.toString(), "");
        }

        if(refreshFor == toRefresh) {
            return createNametag(ChatColor.DARK_GREEN.toString(), "");
        }

        if(faction != null) {
            if(faction.isMember(refreshFor.getName())) {
                return createNametag(ChatColor.DARK_GREEN.toString(), "");
            } else if(HCFConfiguration.maxAllysPerFaction > 0) {
                if(faction.getAllied().contains(targetFac.getUniqueID())) {
                    return createNametag(ChatColor.LIGHT_PURPLE.toString(), "");
                }
            } else {
                return createNametag(ChatColor.YELLOW.toString(), "");
            }
        } else {
            return createNametag(ChatColor.YELLOW.toString(), "");
        }

        return null;
    }
}
