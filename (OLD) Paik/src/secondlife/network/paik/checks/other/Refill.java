package secondlife.network.paik.checks.other;

import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import org.bukkit.inventory.ItemStack;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class Refill {

    public static void handleRefill(Player player, PlayerStats stats, PacketEvent event) {
        if(!ConfigFile.configuration.getBoolean("enabled")) return;
        if(!ConfigFile.configuration.getBoolean("checks.refill")) return;

        if (ServerUtils.isServerLagging()) return;

        int slot = event.getPacket().getIntegers().read(1);

        if(slot >= 36) return;

        ItemStack item = event.getPacket().getItemModifier().read(0);

        if(item == null) return;

        if(item.getType() == Material.POTION || item.getType() == Material.MUSHROOM_SOUP) {
            long delay = System.currentTimeMillis() - stats.getLastClick();

            if(stats.getRefill() > 5) {
                stats.setRefill(0);
                Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Refill (Delay)", player.getPing(), Bukkit.spigot().getTPS()[0]));
            }

            if(stats.getRefillOther() > 15) {
                stats.setRefillOther(0);
                Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Refill (Random)", player.getPing(), Bukkit.spigot().getTPS()[0]));
            }

            int slotDiff = Math.abs(slot - stats.getLastSlot());

            if(delay > 0 && delay <= 200) {
                if(slotDiff < 3) {
                    stats.setRefill(stats.getRefill() + 1);
                    //Message.sendMessage(Color.translate("&aVerbose +1"));
                } else {
                    stats.setRefillOther(stats.getRefillOther() + 1);
                    //Message.sendMessage(Color.translate("&aVerbose +1"));

                    if(stats.getRefill() > 0) {
                        stats.setRefill(stats.getRefill() - 1);
                        //Message.sendMessage(Color.translate("&aVerbose -1"));
                    }
                }
            } else {
                stats.setRefill(0);

                if(stats.getRefillOther() > 1) {
                    stats.setRefillOther(stats.getRefillOther() - 2);
                    //Message.sendMessage(Color.translate("&cVerbose - 2"));
                }
                //Message.sendMessage(Color.translate("&cVerbose 0"));
            }

            //Message.sendMessage(String.valueOf(delay));

            stats.setLastSlot(slot);
            stats.setLastClick(System.currentTimeMillis());
        }
    }
}
