package secondlife.network.meetuplobby.providers;

import org.bukkit.entity.Player;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.providers.NametagProvider;
import secondlife.network.vituz.providers.nametags.NametagInfo;

/**
 * Created by Marko on 21.04.2018.
 */
public class NametagsProvider extends NametagProvider {

    public NametagsProvider() {
        super("", 16);
    }

    @Override
    public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
        if(VituzAPI.hasRanksData(toRefresh) && VituzAPI.hasPunishData(toRefresh)) {
            return createNametag(VituzAPI.getNamePrefix(toRefresh), "");
        }

        return null;
    }
}
