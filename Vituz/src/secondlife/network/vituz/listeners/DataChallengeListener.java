package secondlife.network.vituz.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import secondlife.network.vituz.data.ChallengeData;
import secondlife.network.vituz.utilties.PlayerUtils;

public class DataChallengeListener implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if(!PlayerUtils.isMongoConnected(event)) return;

        ChallengeData data = ChallengeData.getByName(event.getName());

        if (!data.isLoaded()) {
            data.load();
        }

        if(!data.isLoaded()) {
            PlayerUtils.kick(event);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
         ChallengeData.getByName(event.getPlayer().getName()).save();
    }
}
