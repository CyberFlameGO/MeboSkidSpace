package secondlife.network.hcfactions.handlers;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Marko on 10.04.2018.
 */
public class ThrowableCobwebHandler extends Handler implements Listener {

    public static ArrayList<FallingBlock> fallingBlocks = new ArrayList<>();

    public static HashMap<UUID, Long> cooldown;

    public ThrowableCobwebHandler(HCF plugin) {
        super(plugin);

        cooldown = new HashMap<UUID, Long>();

        Bukkit.getPluginManager().registerEvents(this, this.getInstance());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if(player.getItemInHand() == null
                || player.getItemInHand().getItemMeta() == null
                || player.getItemInHand().getItemMeta().getDisplayName() == null) {
            return;
        }

        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&dThrowable Web"))) {
                if(isActive(player)) {
                    event.setCancelled(true);

                    if(player.getItemInHand().getAmount() == 1) {
                        player.getInventory().remove(player.getItemInHand());
                    } else {
                        player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                    }

                    Byte blockByte = 0;
                    FallingBlock block = world.spawnFallingBlock(player.getEyeLocation(), Material.WEB, blockByte);

                    Vector smallerVector = player.getLocation().getDirection().multiply(2);
                    block.setVelocity(smallerVector);

                    fallingBlocks.add(block);
                } else {
                    player.sendMessage(Color.translate("&cYou can't use this for another &l" + DurationFormatUtils.formatDurationWords(getMillisecondsLeft(player), true, false)));
                }
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if(!(event.getEntity() instanceof FallingBlock)) return;
        if(!event.getBlock().isEmpty()) return;

        FallingBlock fallingBlock = (FallingBlock) event.getEntity();

        if(!fallingBlocks.contains(fallingBlock)) return;

        new BukkitRunnable() {
            public void run() {
                fallingBlocks.remove(fallingBlock);

                event.getBlock().setType(Material.AIR);
                event.getBlock().getState().update();

                event.setCancelled(true);
            }
        }.runTaskLater(this.getInstance(), 15 * 20L);
    }

    public static boolean isActive(Player player) {
        return cooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis() < cooldown.get(player.getUniqueId());
    }

    public static void applyCooldown(Player player) {
        cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (15 * 1000));
    }

    public static void stopCooldown(Player player) {
        cooldown.remove(player.getUniqueId());
    }

    public static long getMillisecondsLeft(Player player) {
        if (cooldown.containsKey(player.getUniqueId())) {
            return Math.max(cooldown.get(player.getUniqueId()) - System.currentTimeMillis(), 0L);
        }

        return 0L;
    }
}
