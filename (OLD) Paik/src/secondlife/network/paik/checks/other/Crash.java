package secondlife.network.paik.checks.other;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.handlers.CheatHandler;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class Crash {

    public static void handleBlockPlaceCrash(Player player, PlayerStats stats) {
        if(!ConfigFile.configuration.getBoolean("enabled")) return;
        if(!ConfigFile.configuration.getBoolean("checks.crash")) return;

        if(stats.getBoxer2() > 500) {
            stats.setBoxer2(0);
            Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Crash (Place)", player.getPing(), Bukkit.spigot().getTPS()[0]));
            CheatHandler.handleBan(player);
        }

        if(System.currentTimeMillis() - stats.getLastBlockPacket() > 100) {
            stats.setBoxer2(0);
        }

        stats.setBoxer2(stats.getBoxer2() + 1);
        stats.setLastBlockPacket(System.currentTimeMillis());
    }

    public static void handleAnimationCrash(Player player, PlayerStats stats) {
        if(!ConfigFile.configuration.getBoolean("enabled")) return;
        if(!ConfigFile.configuration.getBoolean("checks.crash")) return;

        if(stats.getBoxer1() > 500) {
            stats.setBoxer1(0);
            Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Crash (Animation)", player.getPing(), Bukkit.spigot().getTPS()[0]));
            CheatHandler.handleBan(player);
        }

        if(System.currentTimeMillis() - stats.getLastArmPacket() > 50) {
            stats.setBoxer1(0);
        }

        stats.setBoxer1(stats.getBoxer1() + 1);
        stats.setLastArmPacket(System.currentTimeMillis());
    }
}
