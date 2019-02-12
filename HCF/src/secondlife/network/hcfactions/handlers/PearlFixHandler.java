package secondlife.network.hcfactions.handlers;

import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Gate;
import org.bukkit.projectiles.ProjectileSource;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.timers.EnderpearlHandler;
import secondlife.network.hcfactions.utilties.Handler;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class PearlFixHandler extends Handler implements Listener {

    public ConcurrentMap<Object, Object> pearlMap = CacheBuilder.newBuilder().expireAfterWrite(15L, TimeUnit.SECONDS).build().asMap();

    public PearlFixHandler(HCF plugin) {
        super(plugin);

        //runCheck();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent e) {
        Projectile proj = e.getEntity();
        ProjectileSource ps = proj.getShooter();

        if(ps instanceof Player && proj instanceof EnderPearl) {
            Player p = (Player) ps;
            EnderPearl pearl = (EnderPearl) proj;

            this.pearlMap.put(p, pearl);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if(this.pearlMap.containsKey(p)) {
            EnderPearl pearl = (EnderPearl) this.pearlMap.get(p);
            pearl.remove();
            this.pearlMap.remove(p);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();

        if(this.pearlMap.containsKey(p)) {
            EnderPearl pearl = (EnderPearl) this.pearlMap.get(p);
            pearl.remove();
            this.pearlMap.remove(p);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if((e.getItem() != null) && (e.getItem().getType() == Material.ENDER_PEARL) && ((e.getAction() == Action.RIGHT_CLICK_BLOCK) || (e.getAction() == Action.RIGHT_CLICK_AIR)) && (e.getAction() == Action.RIGHT_CLICK_BLOCK) && (e.getClickedBlock().getType() == Material.FENCE_GATE) && (!((Gate) e.getClickedBlock().getState().getData()).isOpen())) {
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onClip(PlayerTeleportEvent e) {
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Player p = e.getPlayer();
            Location to = e.getTo().clone();

            to.setX(to.getBlockX() + 0.5D);
            to.setZ(to.getBlockZ() + 0.5D);

            e.setTo(to);

            if((to.getBlock() != null) && ((to.getBlock().getType() == Material.STEP) || (to.getBlock().getType() == Material.WOOD_STEP))) {
                to.setY(to.getY() + 1.0D);
                e.setTo(to);
            }

            if(to.getBlock().getRelative(BlockFace.UP).getType().isSolid()) {
                to.subtract(0.0D, 1.0D, 0.0D);

                if((to.getBlock().getType().isSolid()) || (to.getBlock().getRelative(BlockFace.UP).getType().isSolid())) {
                    if(this.pearlMap.containsKey(p)) {
                        EnderPearl pearl = (EnderPearl) this.pearlMap.get(p);
                        pearl.remove();
                        this.pearlMap.remove(p);
                    }
                    e.setCancelled(true);
                    EnderpearlHandler.stopCooldown(p);
                    p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
                }

                e.setTo(to);
            }
        }
    }
}
