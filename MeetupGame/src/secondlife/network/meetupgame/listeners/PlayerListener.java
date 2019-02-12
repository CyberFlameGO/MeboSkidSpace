package secondlife.network.meetupgame.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.player.PlayerData;
import secondlife.network.meetupgame.state.GameState;
import secondlife.network.meetupgame.utilities.MeetupUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.ItemBuilder;

/**
 * Created by Marko on 11.06.2018.
 */
public class PlayerListener implements Listener {

    private MeetupGame plugin = MeetupGame.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerData data = PlayerData.getByPlayer(player);

        plugin.getInventoryManager().loadInventory(player, plugin.getGameManager().getGameState());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if(MeetupUtils.isState(player)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        if(plugin.getGameManager().getWhitelistedBlocks().contains(event.getBlock().getType())) {
            event.setCancelled(false);
        } else {
            player.sendMessage(Color.translate("&cYou aren't allowed to break this block!"));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if(MeetupUtils.isState(player)) {
            event.setCancelled(true);
            return;
        }

        int max = 90;
        if(event.getBlock().getY() > max) {
            event.setCancelled(true);

            if(player.getLocation().getY() > max + 2) {
                Location finalLocation = player.getLocation();
                finalLocation.setY(max + 1);

                if(finalLocation.getBlock().getRelative(0, 1, 0).isEmpty()) {
                    player.teleport(finalLocation);
                }

                player.sendMessage(Color.translate("&cSky basing isn't allowed!"));
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();

        if(MeetupUtils.isState(player)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if(MeetupUtils.isState(player)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        if(MeetupUtils.isState(player)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        if(!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        if(MeetupUtils.isState(player)) {
            event.setCancelled(true);
            return;
        }
    }
}
