package secondlife.network.paik.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import org.bukkit.inventory.ItemStack;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class AutoBlock {

    public static void handleAutoBlockPlace(Player player, PlayerStats stats) {
        if(stats.getAutoblock() > 0) {
            stats.setAutoblock(stats.getAutoblock() - 1);
            //Message.sendMessage("§cverbose -1 (BLOCK PLACE)");
        }
    }

    public static void handleAutoBlockDig(Player player, PlayerStats stats) {
        if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.autoblock")) {
            if (ServerUtils.isServerLagging()) return;

            if(stats.getAutoblock() > 15) {
                stats.setAutoblock(0);
                Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "AutoBlock (Damage)", player.getPing(), Bukkit.spigot().getTPS()[0]));
            }

            if(System.currentTimeMillis() - stats.getLastUseEntityPacket() < 250) {
                ItemStack item = player.getItemInHand();

                if(item == null) return;

                if(item.getType() == Material.DIAMOND_SWORD
                        || item.getType() == Material.GOLD_SWORD
                        || item.getType() == Material.IRON_SWORD
                        || item.getType() == Material.STONE_SWORD
                        || item.getType() == Material.WOOD_SWORD) {
                    stats.setAutoblock(stats.getAutoblock() + 1);
                    //Message.sendMessage("§averbose +1");
                }
            } else {
                if(stats.getAutoblock() > 0) {
                    stats.setAutoblock(stats.getAutoblock() - 1);
                    //Message.sendMessage("§cverbose -1 (USE ENTITY)");
                }
            }
        }
    }

    public static void handleAutoBlock(Player player, PlayerStats stats) {
        if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.autoblock")) {
            if (ServerUtils.isServerLagging()) return;

            ItemStack item = player.getItemInHand();

            if(item == null) return;

            if(item.getType() == Material.DIAMOND_SWORD
                    || item.getType() == Material.GOLD_SWORD
                    || item.getType() == Material.IRON_SWORD
                    || item.getType() == Material.STONE_SWORD
                    || item.getType() == Material.WOOD_SWORD) {

                if (player.isBlocking() && player.isSprinting()) {
                    if(stats.getAutoblock2() > 50) {
                        stats.setAutoblock2(0);
                        Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "AutoBlock (Walk)", player.getPing(), Bukkit.spigot().getTPS()[0]));
                    }
                } else {
                    stats.setAutoblock2(0);
                }
            }
        }
    }
}
