package secondlife.network.victions.managers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import secondlife.network.victions.Victions;
import secondlife.network.victions.utilities.Manager;
import secondlife.network.vituz.utilties.Color;

/**
 * Created by Marko on 18.07.2018.
 */
public class FactionsManager extends Manager {

    public FactionsManager(Victions plugin) {
        super(plugin);
    }

    public void handleCraftTNT(Player player) {
        Inventory inventory = player.getInventory();

        int sandCount = countItems(inventory, Material.SAND, 0);
        int sulphurCount = countItems(inventory, Material.SULPHUR, 0);
        int sandDivided = sandCount / 4;
        int sulphurDivided = sulphurCount / 5;

        if(sandDivided == 0 || sulphurDivided == 0) {
            player.sendMessage(Color.translate("&eYou don't have enough &dSand &eor &dGunpowder&e."));
            return;
        }

        int maxTNTAmount = Math.min(sandDivided, sulphurDivided);
        int sandExtra = sandCount - (maxTNTAmount * 4);
        int sulphurExtra = sulphurCount - (maxTNTAmount * 5);

        removeInventoryItems(inventory, Material.SAND, sandCount - sandExtra);
        removeInventoryItems(inventory, Material.SULPHUR, sulphurCount - sulphurExtra);

        inventory.addItem(new ItemStack(Material.TNT, maxTNTAmount));

        player.sendMessage(Color.translate("&eYou have crafted &d" + maxTNTAmount + " &eTNT."));
    }

    private int countItems(Inventory inventory, Material material, int durability) {
        ItemStack[] items = inventory.getContents();

        int amount = 0;

        for(ItemStack item : items) {
            if(item != null && item.getType() == material && (item.getDurability() == (short) durability)) {
                amount += item.getAmount();
            }
        }

        return amount;
    }

    private void removeInventoryItems(Inventory inventory, Material material, int amount) {
        for(ItemStack item : inventory.getContents()) {
            if(item != null && item.getType() == material) {
                int newAmount = item.getAmount() - amount;

                if(newAmount > 0) {
                    item.setAmount(newAmount);
                    break;
                } else {
                    inventory.removeItem(item);
                    amount = -newAmount;

                    if(amount == 0) {
                        break;
                    }
                }
            }
        }
    }
}
