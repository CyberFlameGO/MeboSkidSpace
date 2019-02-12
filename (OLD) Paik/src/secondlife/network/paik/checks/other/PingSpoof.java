package secondlife.network.paik.checks.other;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class PingSpoof {

    public static void handlePingSpoof(Player player, PlayerStats stats) {
        if(!ConfigFile.configuration.getBoolean("enabled")) return;
        if(!ConfigFile.configuration.getBoolean("checks.pingspoof")) return;

        if (ServerUtils.isServerLagging()) return;

        int ping = player.getPing();

        int diff = Math.abs(ping - stats.getLastPing());

        if(player.getPing() > 200 && stats.getPingSpoof() > 10) {
            stats.setPingSpoof(0);
            Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "PingSpoof", player.getPing(), Bukkit.spigot().getTPS()[0]));
        }

        if(player.getPing() > 200 && diff > 0 && diff < 11) {
            stats.setPingSpoof(stats.getPingSpoof() + 1);
        } else if(player.getPing() > 200 && diff == 0) {
            stats.setPingSpoof(stats.getPingSpoof() + 2);
        } else {
            if(stats.getPingSpoof() > 1) {
                stats.setPingSpoof(stats.getPingSpoof() - 2);
            }
        }

        stats.setLastPing(ping);
    }
}
