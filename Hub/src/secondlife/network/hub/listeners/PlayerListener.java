package secondlife.network.hub.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import secondlife.network.hub.Hub;
import secondlife.network.hub.managers.QueueManager;
import secondlife.network.hub.utilties.HubUtils;
import secondlife.network.hub.utilties.profile.BukkitProfileStorage;
import secondlife.network.hub.utilties.profile.BukkitProfileUtils;
import secondlife.network.overpass.data.OverpassData;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Tasks;
import secondlife.network.vituz.utilties.item.ItemBuilder;

/**
 * Created by Marko on 28.03.2018.
 */
public class PlayerListener implements Listener {

    private Hub plugin = Hub.getInstance();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if(event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }

        if(BukkitProfileUtils.isEnabled()) {
            BukkitProfileStorage storage = Hub.getInstance().getStorage();

            if(!storage.hasRealPlayer(event.getPlayer())) {
                storage.addRealPlayer(event.getPlayer());
                return;
            }

            if(!storage.isPlayerReal(event.getPlayer())) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Color.translate("&cPlease spell your name correctly!\n&cName of this account is " + storage.getRealPlayerValidName(event.getPlayer()) + "!"));
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getMultiSpawnManager().randomSpawn(player);

        Bukkit.getOnlinePlayers().forEach(other -> {
            if(plugin.getHubManager().getHidingPlayers().contains(other.getUniqueId())) {
                if(!player.isOp() && !player.getUniqueId().equals(other.getUniqueId())) {
                    other.hidePlayer(player);
                }
            }
        });

        Tasks.runLater(() -> {
            HubUtils.resetPlayer(player);
            plugin.getHubManager().handleSetArmor(player);
            VituzNametag.reloadPlayer(player);
            VituzNametag.reloadOthersFor(player);
        }, 1L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getAutoKickManager().handleRemove(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null
                || event.getCurrentItem().getType() == Material.AIR
                || !event.getCurrentItem().hasItemMeta()
                || event.getCurrentItem().getItemMeta() == null) {
            return;
        }

        event.setCancelled(true);

        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(Color.translate("&f "))) {
            return;
        }

        String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
        Player player = (Player) event.getWhoClicked();

        if(event.getClickedInventory().getTitle().equals(plugin.getSelectorManager().getSelectorName())) {
            if(name.equals("UHCMeetup")) {
                name = "UHCMeetup-Lobby";
            }

            if(name.equals("UHC Selector")) {
                player.openInventory(plugin.getSelectorManager().getUhcSelector());
                return;
            }

            if(VituzAPI.getServerData(name) == null || !VituzAPI.getServerData(name).isOnline() || QueueManager.getByServer(name) == null) {
                player.sendMessage(Color.translate("&cQueue of server named '" + name + "' is currently offline."));
                return;
            }

            if(QueueManager.getByServer(name).isPaused()) {
                player.sendMessage(Color.translate("&c" + name + " queue is currently paused!"));
                return;
            }

            QueueManager.getByServer(name).handlePut(player);
        } else if(event.getClickedInventory().getTitle().equals(plugin.getSelectorManager().getSelectorUhcName())) {
            if(VituzAPI.getServerData(name) == null || !VituzAPI.getServerData(name).isOnline() || QueueManager.getByServer(name) == null) {
                player.sendMessage(Color.translate("&cQueue of server named '" + name + "' is currently offline."));
                return;
            }

            if(QueueManager.getByServer(name).isPaused()) {
                player.sendMessage(Color.translate("&c" + name + " queue is currently paused!"));
                return;
            }

            QueueManager.getByServer(name).handlePut(player);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        OverpassData profile = OverpassData.getByName(player.getName());

        if (secondlife.network.overpass.listeners.PlayerListener.doStuff(profile)) {
            event.setCancelled(true);
            return;
        }

        Action action = event.getAction();

        ItemStack stack = player.getItemInHand();

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (stack.getType() == Material.COMPASS) {
                if (VituzAPI.isBanned(player)) {
                    player.sendMessage(Color.translate("&cYour account is currently suspended. To appeal, visit " + Vituz.getInstance().getEssentialsManager().getAppealAt() + " to appeal!"));
                } else {
                    player.openInventory(plugin.getSelectorManager().getServerSelector());
                }
            } else if (stack.getType() == Material.ENDER_PEARL) {
                event.setCancelled(true);

                event.setUseItemInHand(Event.Result.DENY);
                event.setUseInteractedBlock(Event.Result.DENY);

                player.setVelocity(player.getLocation().getDirection().multiply(8.0).setY(1.5));
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 10, 10);

                player.updateInventory();
            } else if (stack.getType() == Material.INK_SACK) {
                int durabilty = stack.getDurability();

                if (durabilty == 8 && stack.getItemMeta() != null && stack.getItemMeta().getDisplayName() != null && stack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "Hide players")) {
                    player.sendMessage(ChatColor.RED + "You have hidden all visible players.");
                    player.setItemInHand(new ItemBuilder(Material.INK_SACK).durability(10).name(ChatColor.GREEN + "Show players").build());

                    for (Player other : Bukkit.getOnlinePlayers()) {
                        if (other.isOp()) continue;
                        if (other.getUniqueId().equals(player.getUniqueId())) continue;

                        player.hidePlayer(other);
                    }

                    plugin.getHubManager().getHidingPlayers().add(player.getUniqueId());
                    plugin.getHubManager().getCooldowns().put(player.getUniqueId(), System.currentTimeMillis());
                } else if (durabilty == 10 && stack.getItemMeta() != null && stack.getItemMeta().getDisplayName() != null && stack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Show players")) {
                    if (plugin.getHubManager().getCooldowns().containsKey(player.getUniqueId())) {
                        if (System.currentTimeMillis() - plugin.getHubManager().getCooldowns().get(player.getUniqueId()) < 3000) {
                            player.sendMessage(ChatColor.RED + "You must wait before toggling player visibility again.");
                            return;
                        } else {
                            plugin.getHubManager().getCooldowns().remove(player.getUniqueId());
                        }
                    }

                    player.sendMessage(ChatColor.RED + "You have made all players visible.");
                    player.setItemInHand(new ItemBuilder(Material.INK_SACK).durability(8).name(ChatColor.GRAY + "Hide players").build());

                    for (Player other : Bukkit.getOnlinePlayers()) {
                        if (other.isOp()) continue;
                        if (other.getUniqueId().equals(player.getUniqueId())) continue;

                        player.showPlayer(other);
                    }

                    plugin.getHubManager().getHidingPlayers().remove(player.getUniqueId());
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

        if((event.getCurrentItem().getType() == Material.COMPASS) || (event.getCurrentItem().getType() == Material.ENDER_PEARL) || (event.getCurrentItem().getType() == Material.INK_SACK) || (event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE)) event.setCancelled(true);
        if((event.getCurrentItem().getType() == Material.LEATHER_CHESTPLATE) || (event.getCurrentItem().getType() == Material.LEATHER_LEGGINGS) || (event.getCurrentItem().getType() == Material.LEATHER_BOOTS)  || (event.getCurrentItem().getType() == Material.BEDROCK)) event.setCancelled(true);
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
