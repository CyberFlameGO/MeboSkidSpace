package secondlife.network.victions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import secondlife.network.victions.Victions;
import secondlife.network.victions.player.FactionsData;
import secondlife.network.victions.utilities.GlowEnchantment;
import secondlife.network.vituz.utilties.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marko on 18.07.2018.
 */
public class SellWandListener implements Listener {

    private Victions plugin = Victions.getInstance();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.isCancelled()) return;

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player player = event.getPlayer();

            if(player.getItemInHand() == null
                    || !player.getItemInHand().hasItemMeta()
                    || !player.getItemInHand().getItemMeta().hasLore()) {
                return;
            }

            ItemStack stack = new ItemStack(Material.DIAMOND_HOE);
            ItemMeta meta = stack.getItemMeta();
           
            List<String> lore = new ArrayList<>();
            lore.add("&fRight click chest with");
            lore.add("&fthis item to sell it's contents.");

            List<String> playerLore = new ArrayList<>();
            playerLore.addAll(player.getItemInHand().getItemMeta().getLore());

            List<String> itemLore = new ArrayList<>();
            itemLore.addAll(lore);

            itemLore.forEach(iLore -> {
                if(playerLore.contains(iLore)) {
                    playerLore.remove(iLore);
                }
            });

            int uses = -1;

            if(!playerLore.isEmpty() && playerLore.get(0).startsWith(Color.translate("&fUses: &d" + uses))) {
                lore.add(playerLore.get(0));

                String toModify = ChatColor.stripColor(playerLore.get(0));

                toModify = toModify.replaceAll("[^\\d.]", "");
                uses = Integer.parseInt(toModify);
            }

            meta.setLore(lore);
            meta.setDisplayName(Color.translate("&dSell Wand"));
            stack.setAmount(player.getItemInHand().getAmount());
            stack.setItemMeta(meta);

            if(!player.getItemInHand().equals(stack)) {
                return;
            }

            event.setCancelled(true);

            Block block = event.getClickedBlock();

            if(block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST)) {
                BlockBreakEvent toCall = new BlockBreakEvent(event.getClickedBlock(), player);

                Bukkit.getServer().getPluginManager().callEvent(toCall);

                if(toCall.isCancelled()) {
                    return;
                }

                Chest chest = (Chest) block.getState();
                Inventory inventory = chest.getInventory();

                int slot = 0;
                int totalSale = 0;

                for(ItemStack stackInChest : inventory) {
                    if(stackInChest == null
                            || stackInChest.getType().equals(Material.AIR)
                            || stackInChest.getType() == null) {
                        slot++;
                    } else {
                        String type = stackInChest.getType().toString().toLowerCase();
                        type = type.replace("_", "");

                        String dataCode = String.valueOf(stackInChest.getData().getData());

                        if(plugin.getSellWandManager().getPrices().containsKey(type)) {
                            chest.getInventory().setItem(slot, new ItemStack(Material.AIR));
                            totalSale = totalSale + plugin.getSellWandManager().getPrices().get(type) * stackInChest.getAmount();
                        } else if(plugin.getSellWandManager().getPrices().containsKey(type + ":" + dataCode)) {
                            chest.getInventory().setItem(slot, new ItemStack(Material.AIR));
                            totalSale = totalSale + plugin.getSellWandManager().getPrices().get(type + ":" + dataCode) * stackInChest.getAmount();
                        }

                        slot++;
                    }
                }

                FactionsData data = FactionsData.getByName(player.getName());
                data.setBalance(data.getBalance() + totalSale);

                player.sendMessage(Color.translate("&eYou have received &d$ " + totalSale + "&e."));

                if(uses > 0) {
                    uses--;

                    if(uses <= 0) {
                        player.setItemInHand(new ItemStack(Material.AIR));
                        player.sendMessage(Color.translate("&cYour sell wand has no more uses left."));
                    } else {
                        ItemStack toReplace = new ItemStack(Material.DIAMOND_HOE);
                        ItemMeta toReplaceMeta = toReplace.getItemMeta();
                        List<String> toReplaceLore = new ArrayList<>();

                        toReplaceLore.add("&fRight click chest with");
                        toReplaceLore.add("&fthis item to sell it's contents.");

                        Enchantment enchantment = GlowEnchantment.getGlow();

                        toReplaceMeta.addEnchant(enchantment, 1, true);

                        toReplaceLore.add("");
                        toReplaceLore.add("&fUses: &d" + uses);

                        toReplaceMeta.setLore(toReplaceLore);
                        toReplaceMeta.setDisplayName(Color.translate("&dSell Wand"));

                        toReplace.setAmount(player.getItemInHand().getAmount());
                        toReplace.setItemMeta(toReplaceMeta);

                        player.setItemInHand(toReplace);

                        player.sendMessage(Color.translate("&eYour &dSell Wand &enow has &d" + uses + " &euses left."));
                    }
                }
            }
        }
    }
}
