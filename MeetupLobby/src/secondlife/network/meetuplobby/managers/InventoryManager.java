package secondlife.network.meetuplobby.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import secondlife.network.meetuplobby.MeetupLobby;
import secondlife.network.meetuplobby.utilities.Manager;
import secondlife.network.vituz.utilties.ItemBuilder;

/**
 * Created by Marko on 10.06.2018.
 */
public class InventoryManager extends Manager {

    public InventoryManager(MeetupLobby plugin) {
        super(plugin);
    }

    public void loadInventory(Player player) {
        if(plugin.getQueueManager().getSoloQueue().contains(player.getUniqueId()) || plugin.getQueueManager().getDuoQueue().contains(player.getUniqueId())) {
            player.getInventory().setItem(0, new ItemBuilder(Material.INK_SACK).durability(10).name("&c&lLeave Queue").build());
            player.getInventory().setItem(8, new ItemBuilder(Material.WATCH).name("&b&lSpectate Match").build());
        } else {
            player.getInventory().setItem(0, new ItemBuilder(Material.INK_SACK).durability(8).name("&a&lJoin Queue").build());
            player.getInventory().setItem(8, new ItemBuilder(Material.WATCH).name("&b&lSpectate Match").build());
        }
    }

    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, "Select queue");

        inventory.setItem(2, new ItemBuilder(Material.SKULL_ITEM).name("&cSolos").build());
        inventory.setItem(6, new ItemBuilder(Material.SKULL_ITEM).amount(2).name("&cDuos").build());

        for(int i = 0; i < inventory.getSize(); i++) {
            if(inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).build());
            }
        }

        return inventory;
    }

}
