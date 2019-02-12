package secondlife.network.overpass.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.overpass.Overpass;
import secondlife.network.overpass.data.OverpassData;
import secondlife.network.overpass.utilties.OverpassUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.PlayerUtils;

/**
 * Created by Marko on 10.05.2018.
 */
public class OverpassDataListener implements Listener {

    private Overpass plugin = Overpass.getInstance();

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if(!PlayerUtils.isMongoConnected(event)) return;

        if(!OverpassUtils.isNicknameValid(event.getName())) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Color.translate("&cInvalid name!"));
            return;
        }

        OverpassData data = OverpassData.getByName(event.getName());

        if (!data.isLoaded()) {
            data.load();
        }

        if(!data.isLoaded()) {
            PlayerUtils.kick(event);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        plugin.getOverpassManager().handleRemoveFromList(name);
        OverpassData.getByName(name).save();
    }

    @EventHandler
    public void onPlayeJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        OverpassData overpassData = OverpassData.getByName(player.getName());

        if(!overpassData.isLoaded()) {
            player.kickPlayer(Color.translate("&cYour data hasn't been loaded. Please try joining again!"));
            return;
        }

        overpassData.setNeedLogin(true);

        new BukkitRunnable() {
            public void run() {
                if (overpassData.isFullyRegistered()) {
                    if(OverpassUtils.isPremium(player)) {
                        overpassData.setNeedLogin(false);
                        player.sendMessage(Color.translate("&eYou have logged in."));
                        OverpassUtils.callLoginEvent(player);
                    } else {
                        player.sendMessage(Color.translate("&ePlease use &d/login <password> &eto login."));
                    }
                } else if(overpassData.isRegister() || overpassData.isNeedToEnterCode()) {
                    player.sendMessage(Color.translate("&ePlease use &d/code <code> &eto complete your registration."));
                    player.sendMessage(Color.translate("&eYou can find your code at your email."));
                    player.sendMessage(Color.translate("&eMake sure to check your spam folder and other folders in your email!"));
                }
            }
        }.runTaskLater(plugin, 10L);

        new BukkitRunnable() {
            public void run() {
                if(!overpassData.isFullyRegistered()) {
                    player.kickPlayer(Color.translate("&cYour time has expired!"));
                }
            }
        }.runTaskLater(plugin, 3600L);
    }
}
