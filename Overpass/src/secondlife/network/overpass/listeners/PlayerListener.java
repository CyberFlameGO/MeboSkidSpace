package secondlife.network.overpass.listeners;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import secondlife.network.overpass.data.OverpassData;

/**
 * Created by Marko on 10.05.2018.
 */
public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        OverpassData overpassData = OverpassData.getByName(event.getPlayer().getName());

        String message = event.getMessage().toLowerCase();

        if(message.startsWith("/login")
                || message.startsWith("/register")
                || message.startsWith("/code")
                || message.startsWith("/l")
                || message.startsWith("/reg")
                || message.startsWith("/auth")
                || message.startsWith("/security ")) return;

        if(doStuff(overpassData)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreproccessEvent(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();

        if(message.startsWith("/login")
                || message.startsWith("/register")
                || message.startsWith("/code")
                || message.startsWith("/l")
                || message.startsWith("/reg")
                || message.startsWith("/auth")
                || message.startsWith("/security ")) return;

        OverpassData overpassData = OverpassData.getByName(event.getPlayer().getName());

        if(doStuff(overpassData)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        OverpassData overpassData = OverpassData.getByName(event.getPlayer().getName());

        if(doStuff(overpassData)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockBreakEvent event) {
        OverpassData overpassData = OverpassData.getByName(event.getPlayer().getName());

        if(doStuff(overpassData)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        OverpassData overpassData = OverpassData.getByName(event.getPlayer().getName());

        if(doStuff(overpassData)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        OverpassData overpassData = OverpassData.getByName(event.getPlayer().getName());

        if(doStuff(overpassData)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

        if(player == null) return;

        OverpassData overpassData = OverpassData.getByName(player.getName());

        if(doStuff(overpassData)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        OverpassData overpassData = OverpassData.getByName(player.getName());

        if(doStuff(overpassData)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        OverpassData overpassData = OverpassData.getByName(player.getName());

        if(doStuff(overpassData)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        OverpassData overpassData = OverpassData.getByName(player.getName());

        if(doStuff(overpassData)) {
            event.setCancelled(true);
            return;
        }
    }

    public static boolean doStuff(OverpassData overpassData) {
        if(overpassData.isNeedLogin() || overpassData.isNeedToEnterCode() || !overpassData.isRegister() ||  !overpassData.isFullyRegistered()) {
            return true;
        }
        
        return false;
    }
}
