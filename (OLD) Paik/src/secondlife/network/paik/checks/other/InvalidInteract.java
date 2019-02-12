package secondlife.network.paik.checks.other;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class InvalidInteract {

    public static void handleInvalidInteract(Player player, PlayerStats stats, Action action) {
        if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.invalidinteract")) {
            if (ServerUtils.isServerLagging()) return;

            if (stats.getInvalidInteract() > 50) {
                stats.setInvalidInteract(0);
                Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "InvalidInteract", player.getPing(), Bukkit.spigot().getTPS()[0]));
            }

            if (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK) {
                Block block = player.getTargetBlock(null, 6);
                if (System.currentTimeMillis() - stats.getJoined() > 1500 && block != null && block.getType() == Material.AIR) {
                    stats.setInvalidInteract(stats.getInvalidInteract() + 1);
                } else {
                    stats.setInvalidInteract(0);
                }
            }
        }
    }
}
