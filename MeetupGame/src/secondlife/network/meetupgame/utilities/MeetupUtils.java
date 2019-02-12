package secondlife.network.meetupgame.utilities;

import org.bukkit.entity.Player;
import secondlife.network.meetupgame.player.PlayerData;
import secondlife.network.meetupgame.state.PlayerState;

/**
 * Created by Marko on 12.06.2018.
 */
public class MeetupUtils {

    public static boolean isState(Player player) {
        PlayerData data = PlayerData.getByPlayer(player);

        if(data == null) return false;

        if(!data.getPlayerState().equals(PlayerState.PLAYING)) {
            return false;
        }

        return true;
    }
}
