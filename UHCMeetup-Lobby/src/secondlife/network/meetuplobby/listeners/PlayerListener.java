package secondlife.network.meetuplobby.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import secondlife.network.meetuplobby.MeetupLobby;
import secondlife.network.meetuplobby.utilties.MeetupUtils;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.status.ServerData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.ServerUtils;
import secondlife.network.vituz.utilties.StringUtils;
import secondlife.network.vituz.utilties.Tasks;
import secondlife.network.vituz.utilties.item.ItemBuilder;

/**
 * Created by Marko on 23.07.2018.
 */
public class PlayerListener implements Listener {

    private MeetupLobby plugin = MeetupLobby.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        VituzNametag.reloadPlayer(player);
        VituzNametag.reloadOthersFor(player);

        MeetupUtils.clearPlayer(player);

        player.teleport(StringUtils.destringifyLocation(plugin.getConfig().getString("spawn-location")));

        Tasks.runLater(() -> {
            player.getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).name("&aJoin UHC Game").build());
            player.getInventory().setItem(7, new ItemBuilder(Material.EMERALD).name("&bView Leaderboards").build());
            player.getInventory().setItem(8, new ItemBuilder(Material.WATCH).name("&3Edit Settings").build());
            player.updateInventory();
        }, 1L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null
                || event.getCurrentItem().getType() == Material.AIR
                || !event.getCurrentItem().hasItemMeta()
                || event.getCurrentItem().getItemMeta() == null
                || !event.getClickedInventory().getTitle().equals(plugin.getInventoryManager().getGameInventory().getName())) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(Color.translate("&f "))) {
            return;
        }

        String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
        Player player = (Player) event.getWhoClicked();

        ServerData serverData = ServerData.getByName(name);

        if(serverData == null) {
            player.sendMessage(Color.translate("&cServer isn't setup!"));
            return;
        }

        ServerUtils.sendToServer(player, name);
        player.sendMessage(Color.translate("&eYou have been sent to &d" + name + "&e."));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(event.getAction().name().startsWith("RIGHT_")) {
            ItemStack item = event.getItem();

            if(item == null) {
                return;
            }

            switch (item.getType()) {
                case DIAMOND_SWORD:
                    player.openInventory(plugin.getInventoryManager().getGameInventory());
                    break;
                case EMERALD:
                    player.openInventory(plugin.getInventoryManager().getLeaderboardInventory());
                    break;
                case WATCH:
                    player.performCommand("settings");
                    break;
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
    public void onPlayerPreprocces(PlayerCommandPreprocessEvent event) {
        if(VituzAPI.isBanned(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
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
