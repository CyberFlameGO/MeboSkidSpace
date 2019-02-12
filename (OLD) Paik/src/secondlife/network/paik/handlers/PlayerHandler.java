package secondlife.network.paik.handlers;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import secondlife.network.paik.Paik;
import secondlife.network.paik.checks.combat.*;
import secondlife.network.paik.checks.movement.GroundSpoof;
import secondlife.network.paik.checks.movement.Inventory;
import secondlife.network.paik.checks.movement.NoSlowdown;
import secondlife.network.paik.checks.movement.Speed;
import secondlife.network.paik.checks.movement.fly.FlyA;
import secondlife.network.paik.checks.movement.fly.FlyB;
import secondlife.network.paik.checks.other.InvalidInteract;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.handlers.data.PlayerStatsHandler;
import secondlife.network.paik.handlers.events.PlayerMoveByBlockEvent;
import secondlife.network.paik.utils.Handler;
import secondlife.network.paik.utils.ServerUtils;

public class PlayerHandler  extends Handler implements Listener {

    public PlayerHandler(Paik plugin) {
        super(plugin);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerStats stats = PlayerStatsHandler.getStats(player);
        Action action = event.getAction();

        AutoClicker.handleAutoClickInteract(player, stats, action);
        FastBow.handleFastBowInteract(player, stats);
        InvalidInteract.handleInvalidInteract(player, stats, action);
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if(event.isCancelled()) return;
        if(!(event.getEntity() instanceof Player)) return;

        Arrow arrow = (Arrow) event.getProjectile();
        Player player = (Player) arrow.getShooter();
        PlayerStats stats = PlayerStatsHandler.getStats(player);

        double power = arrow.getVelocity().length();

        FastBow.handleFastBowShoot(player, stats, power);
        NoSlowdown.handleNoSlowdownShooting(player, stats);
    }
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();
        PlayerStats stats = PlayerStatsHandler.getStats(player);

        FastEat.handleFastEat(player, stats);
        NoSlowdown.handleNoSlowdownEating(player, stats);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();
        PlayerStats stats = PlayerStatsHandler.getStats(player);

        Killaura.handleKillaura(player, stats);
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if(event.isCancelled()
                || event.getEntityType() != EntityType.PLAYER
                || event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED)
            return;

        Player player = (Player) event.getEntity();
        PlayerStats stats = PlayerStatsHandler.getStats(player);

        Regen.handleRegen(player, stats);
    }

    @EventHandler
    public void onPlayerMoveByBlockEvent(PlayerMoveByBlockEvent event) {
        Player player = event.getPlayer();
        PlayerStats stats = PlayerStatsHandler.getStats(player);

        FlyA.handleFly(player, stats, event);
        FlyB.handleFly(player, stats, event);
        GroundSpoof.handleGroundSpoof(player, stats, event);
        Inventory.handleInventoryMove(player, stats, event);
        Speed.handleSpeed(player, stats, event);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(!player.hasAchievement(Achievement.OPEN_INVENTORY)) return;

        player.removeAchievement(Achievement.OPEN_INVENTORY);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        PlayerStats stats = PlayerStatsHandler.getStats(event.getEntity());

        stats.setInventoryOpen(false);
    }

    @EventHandler
    public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
        if(event.getAchievement() != Achievement.OPEN_INVENTORY) return;

        event.setCancelled(true);

        Player player = event.getPlayer();

        if(ServerUtils.isServerLagging()
                || player.getGameMode() == GameMode.CREATIVE
                || player.getAllowFlight())
            return;

        PlayerStats stats = PlayerStatsHandler.getStats(player);

        stats.setInventoryOpen(true);

        Killaura.teleportBot(player);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        PlayerStats stats = PlayerStatsHandler.getStats(player);

        stats.setInventoryOpen(true);
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        PlayerStats stats = PlayerStatsHandler.getStats(event.getPlayer());

        stats.setInventoryOpen(false);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if(event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;

        PlayerStats stats = PlayerStatsHandler.getStats(event.getPlayer());

        stats.setInventoryOpen(false);
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if(event.isCancelled()) return;
        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        PlayerStats stats = PlayerStatsHandler.getStats(player);

        Inventory.handleAutoPotion(player, stats);
    }
}
