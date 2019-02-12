package secondlife.network.vituz.listeners;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.data.CrateData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CrateListener implements Listener {

    private Vituz plugin = Vituz.getInstance();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        CrateData crate = CrateData.getByKey(event.getItemInHand());

        if(crate != null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Color.translate("&cYou can't place crate keys."));
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        if (inventory.getTitle().contains("Items - 1/1") &&  player.hasPermission(Permission.OP_PERMISSION)) {
            CrateData crate = CrateData.getByName(ChatColor.stripColor(inventory.getItem(4).getItemMeta().getLore().get(0).replace("SOTW: ", "")));
            if (crate != null) {
                List<ItemStack> toAdd = new ArrayList<>();

                for (int i = 9; i < inventory.getSize(); i++) {
                    ItemStack itemStack = inventory.getItem(i);
                    if (itemStack != null && itemStack.getType() != Material.AIR) {
                        toAdd.add(itemStack);
                    }
                }

                crate.getItems().clear();
                crate.getItems().addAll(toAdd);
            }
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        if (inventory.getTitle().contains("Items - 1/1") &&  player.hasPermission(Permission.OP_PERMISSION)) {
            if (event.getRawSlot() <= 8) {
                event.setCancelled(true);
            }

            if (event.getClick().name().contains("SHIFT")) {
                ItemStack itemStack = event.getCurrentItem();
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    Inventory clickedInventory = event.getClickedInventory();

                    if (clickedInventory != null && clickedInventory.contains(itemStack) && clickedInventory.equals(inventory)) {
                        return;
                    }

                    event.setCancelled(true);
                    player.getInventory().removeItem(itemStack);

                    int position = 0;
                    for (int i = 0; i < inventory.getSize(); i++) {
                        if (i > 8) {
                            ItemStack slot = inventory.getItem(i);
                            if (slot == null || slot.getType() == Material.AIR) {
                                position = i;
                                break;
                            }
                        }
                    }

                    inventory.setItem(position, itemStack);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK ) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();

            if (block.getType() == Material.ENDER_CHEST) {
                event.setCancelled(true);
                if (event.getItem() != null && event.getItem().getType() != Material.AIR) {
                    CrateData crate = CrateData.getByKey(event.getItem());

                    if (crate != null) {
                        int x = event.getClickedBlock().getX();
                        int y = event.getClickedBlock().getY();
                        int z = event.getClickedBlock().getZ();

                        if(plugin.getCrateManager().doesExists(x, y, z)) {
                            List<ItemStack> finalLoot = new ArrayList<>();
                            List<String> finalLootName = new ArrayList<>();

                            if(player.getInventory().firstEmpty() == -1) {
                                for(int i = 0; i < 3 + plugin.getCrateManager().getBonus(player); i++) {
                                    player.getLocation().getWorld().dropItemNaturally(player.getLocation(), crate.getItems().get(new Random().nextInt(crate.getItems().size())));
                                    player.sendMessage(Color.translate("&cCrate items were dropped on the ground because your inventory was full."));

                                    plugin.getCrateManager().setupBroadcast(player, crate, finalLoot, finalLootName);
                                }
                            } else {
                                for(int i = 0; i < 3 + plugin.getCrateManager().getBonus(player); i++) {
                                    player.getInventory().addItem(crate.getItems().get(new Random().nextInt(crate.getItems().size())));
                                    player.updateInventory();

                                    plugin.getCrateManager().setupBroadcast(player, crate, finalLoot, finalLootName);
                                }
                            }

                            plugin.getCrateManager().broadcast(player, crate, finalLootName);

                            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);

                            ItemStack itemStack = player.getItemInHand();
                            if (itemStack.getAmount() > 1) {
                                itemStack.setAmount(itemStack.getAmount() - 1);
                            } else {
                                player.setItemInHand(new ItemStack(Material.AIR));
                            }
                        }
                    }
                }
            }
        }
    }
}
