package secondlife.network.uhc.providers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import secondlife.network.uhc.managers.PartyManager;
import secondlife.network.uhc.managers.ScenarioManager;
import secondlife.network.uhc.party.Party;
import secondlife.network.uhc.scenario.type.NoCleanScenario;
import secondlife.network.vituz.providers.NametagProvider;
import secondlife.network.vituz.providers.nametags.NametagInfo;
import secondlife.network.vituz.utilties.Color;

/**
 * Created by Marko on 01.04.2018.
 */
public class NametagsProvider extends NametagProvider {

    public NametagsProvider() {
        super("", 16);
    }

    @Override
    public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
        Party party = PartyManager.getByPlayer(toRefresh);

        if(NoCleanScenario.isActive(toRefresh)) {
            return createNametag(ChatColor.GOLD.toString(), "");
        }

        if(ScenarioManager.getByName("Seasons").isEnabled()) {
            return createNametag(Color.translate("&k"), "");
        }

        if(PartyManager.isEnabled()) {
            if(party != null) {
                if(party.getPlayers().contains(refreshFor.getName())) {
                    return createNametag(ChatColor.DARK_GREEN.toString(), "");
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
