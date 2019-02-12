package secondlife.network.meetupgame.providers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import secondlife.network.meetupgame.scenario.type.NoCleanScenario;
import secondlife.network.vituz.providers.NametagProvider;
import secondlife.network.vituz.providers.nametags.NametagInfo;

/**
 * Created by Marko on 01.04.2018.
 */
public class NametagsProvider extends NametagProvider {

    public NametagsProvider() {
        super("", 16);
    }

    @Override
    public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
        if(NoCleanScenario.isActive(toRefresh)) {
            return createNametag(ChatColor.GOLD.toString(), "");
        }

        return null;
    }
}
