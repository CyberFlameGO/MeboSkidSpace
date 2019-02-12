package secondlife.network.meetuplobby.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import secondlife.network.meetuplobby.MeetupLobby;
import secondlife.network.meetuplobby.utilities.OfflinePlayer;
import secondlife.network.meetuplobby.party.Party;
import secondlife.network.meetuplobby.utilities.MeetupUtils;
import secondlife.network.vituz.utilties.Color;

import java.util.Iterator;

/**
 * Created by Marko on 10.06.2018.
 */
public class PlayerListener {

    private MeetupLobby plugin = MeetupLobby.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        MeetupUtils.resetPlayer(player);

        plugin.getInventoryManager().loadInventory(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Party party = plugin.getPartyManager().getByUuid(player.getUniqueId());

        if(party != null) {
            if (party.getLeader().equals(player.getUniqueId())) {
                plugin.getPartyManager().getParties().remove(party.getLeader());

                for(Player partyPlayer : plugin.getPartyManager().getPlayersFromParty(party)) {
                    partyPlayer.sendMessage(Color.translate( "&eThe party has been disbanded."));
                }
            } else {
                Iterator<OfflinePlayer> iterator = party.getPlayers().iterator();

                while(iterator.hasNext()) {
                    OfflinePlayer offlinePlayer = iterator.next();

                    if(offlinePlayer.getUuid().equals(player.getUniqueId())) {
                        iterator.remove();
                        break;
                    }
                }

                for(Player partyPlayer : plugin.getPartyManager().getPlayersFromParty(party)) {
                    partyPlayer.sendMessage(Color.translate( "&d" + player.getName() + " &ehas left the party."));
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getPlayer().isOp()) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.getPlayer().isOp()) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryAction action = event.getAction();

        if(action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.SWAP_WITH_CURSOR) {
            event.setCancelled(true);
        }

        if(event.getSlotType() == InventoryType.SlotType.OUTSIDE) return;
        if(event.getCurrentItem().getType() == Material.AIR) return;

        if((event.getCurrentItem().getType() == Material.INK_SACK) || (event.getCurrentItem().getType() == Material.WATCH) || (event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE)) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if(event.getBlock().getType() != Material.ICE
                || event.getBlock().getType() != Material.SNOW
                || event.getBlock().getType() != Material.SNOW_BLOCK) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWeatherChange(WeatherChangeEvent event) {
        if(!event.toWeatherState()) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        event.setCancelled(true);
    }
}
