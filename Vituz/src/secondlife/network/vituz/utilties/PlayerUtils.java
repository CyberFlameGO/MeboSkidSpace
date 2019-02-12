package secondlife.network.vituz.utilties;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.managers.DatabaseManager;

public class PlayerUtils {

    public static void kick(AsyncPlayerPreLoginEvent event) {
        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        event.setKickMessage(Color.translate("&cYour data hasn't been loaded. Please try re-joining!"));
        return;
    }

    public static void kick(PlayerJoinEvent event) {
        event.getPlayer().kickPlayer(Color.translate("&cYour data hasn't been loaded. Please try re-joining!"));
        return;
    }

    public static boolean isMongoConnected(AsyncPlayerPreLoginEvent event) {
        if(!Vituz.getInstance().getDatabaseManager().isConnected()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Color.translate("&cServer is setting up..."));
            return false;
        }

        return true;
    }

    public static boolean isMongoConnected() {
        if(!Vituz.getInstance().getDatabaseManager().isConnected()) {
            return false;
        }

        return true;
    }

    public static void checkMongo(PlayerJoinEvent event) {
        if(!Vituz.getInstance().getDatabaseManager().isConnected()) {
            event.getPlayer().kickPlayer(Color.translate("&cServer is setting up..."));
            return;
        }
    }
}
