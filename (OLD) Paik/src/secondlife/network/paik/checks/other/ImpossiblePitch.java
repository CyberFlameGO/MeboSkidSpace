package secondlife.network.paik.checks.other;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.handlers.CheatHandler;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class ImpossiblePitch {

    public static void handleImpossiblePitch(Player player, PlayerStats stats, float pitch) {
        if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.impossiblepitch")) {
            if(Math.abs(pitch) > 90.1 && System.currentTimeMillis() - stats.getJoined() > 1500 && !ServerUtils.isServerLagging()) {
                Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "ImpossiblePitch", player.getPing(), Bukkit.spigot().getTPS()[0]));

                if(!ConfigFile.configuration.getBoolean("autobans") || player.hasPermission("secondlife.staff")) return;

                CheatHandler.handleBan(player);
            }
        }
    }
}
