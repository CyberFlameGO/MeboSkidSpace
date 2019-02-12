package secondlife.network.victions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import secondlife.network.victions.Victions;
import secondlife.network.victions.VictionsAPI;
import secondlife.network.vituz.utilties.Color;

/**
 * Created by Marko on 19.07.2018.
 */
public class ChunkBusterListener implements Listener {

    private Victions plugin = Victions.getInstance();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();

        ItemStack item = event.getItemInHand();
        if(item == null || item.getType() == Material.AIR) return;
        if(!item.hasItemMeta()) return;
        if(!item.getItemMeta().getDisplayName().equals(Color.translate("&dChunk Buster"))) return;

        event.setCancelled(true);

        if(VictionsAPI.getByFaction(player) == null) {
            player.sendMessage(Color.translate("&cYou aren't in a faction."));
            return;
        }

        Block block = event.getBlockPlaced();

        int blockX = block.getChunk().getX() << 4;
        int blockZ = block.getChunk().getZ() << 4;

        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                for(int x = blockX; x < blockX + 16; ++x) {
                    for(int z = blockZ; z < blockZ + 16; ++z) {
                        for(int y = 0; y < 255; ++y) {
                            World world = block.getWorld();
                            Block blockAt = world.getBlockAt(x, y, z);

                            if(!blockAt.getType().equals(Material.BEDROCK)) {
                                blockAt.setType(Material.AIR);
                            }
                        }
                    }
                }
            }, 0L);
        }, 10L);
    }
}
