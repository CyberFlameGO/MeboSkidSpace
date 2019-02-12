package secondlife.network.victions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import secondlife.network.victions.Victions;
import secondlife.network.victions.player.FactionsData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.StringUtils;

/**
 * Created by Marko on 18.07.2018.
 */
public class PlayerListener implements Listener {

    private Victions plugin = Victions.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FactionsData data = FactionsData.getByName(player.getName());

        if(data.isNightVision()) {
            if(!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
            }
        } else {
            if(player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FactionsData data = FactionsData.getByName(player.getName());

        data.save();

        if(plugin.getPlayerManager().isSpawnTagActive(player) && !player.hasMetadata("LogoutCommand")) {
            player.setHealth(0.0D);
        }

        data.cancelHome(player);
        data.cancelLogout(player);
        data.cancelPearl(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        FactionsData data = FactionsData.getByName(player.getName());

        data.cancelPearl(player);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            FactionsData data = FactionsData.getByName(player.getName());

            if(data != null) {
                if(data.isHomeActive(player)) {
                    data.cancelHome(player);
                    player.sendMessage(Color.translate("&eTeleport canceled because you were damaged."));
                }

                if(data.isLogoutActive(player)) {
                    data.cancelLogout(player);
                    player.sendMessage(Color.translate("&eTeleport canceled because you were damaged."));
                }

                if(data.isNeedToTeleport()) {
                    data.setNeedToTeleport(false);
                    player.sendMessage(Color.translate("&eTeleport canceled because you were damaged."));
                }

                if(data.isJellyLegs() && event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        FactionsData data = FactionsData.getByName(player.getName());

        if(player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }

        if(!event.hasItem()) {
            return;
        }

        if(event.getAction().name().startsWith("RIGHT_")) {
            ItemStack stack = event.getItem();

            switch (stack.getType()) {
                case ENDER_PEARL: {
                    if(data.isPearlActive(player)) {
                        event.setUseItemInHand(Event.Result.DENY);
                        player.sendMessage(Color.translate("&cYou can't use this for another &l" + StringUtils.getRemaining(data.getPearlMillisecondsLeft(player), true) + "!"));
                    } else {
                        data.applyPearlCooldown(player);
                    }
                    break;
                }
            }
        }
    }

    /*@EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Item item = event.getItem();
        ItemStack stack = item.getItemStack();
        Player player = event.getPlayer();

        if(stack.getType() == Material.POTION) {
            event.setCancelled(true);

            if(player.getInventory().firstEmpty() == -1) {
                player.getInventory().addItem(stack);
                item.remove();
            }
        }
    }*/

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();

        if(inventory != null && inventory.getType() == InventoryType.BREWING) {
            if(event.getClick() == ClickType.NUMBER_KEY && (event.getRawSlot() == 0 || event.getRawSlot() == 1 || event.getRawSlot() == 2)) {
                event.setCancelled(true);
                return;
            }

            if(event.getClick().name().contains("SHIFT") && event.getCurrentItem().getAmount() > 1) {
                Player player = (Player) event.getWhoClicked();
                ItemStack stack = event.getCurrentItem();
                ItemStack newStack = new ItemStack(stack);

                newStack.setAmount(stack.getAmount() - 1);
                stack.setAmount(1);

                Bukkit.getScheduler().runTask(plugin, () -> {
                    if(player.getInventory().getItem(event.getSlot()) == null) {
                        player.getInventory().setItem(event.getSlot(), newStack);
                    } else {
                        stack.setAmount(newStack.getAmount() + 1);
                    }

                    player.updateInventory();
                });
            }
        }
    }
}
