package secondlife.network.meetuplobby.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import secondlife.network.meetuplobby.MeetupLobby;

/**
 * Created by Marko on 10.06.2018.
 */
public class InventoryListener {

    private MeetupLobby plugin = MeetupLobby.getInstance();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        ItemStack stack = event.getCurrentItem();

        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !event.getCurrentItem().hasItemMeta()) return;
        if(event.getCurrentItem().getItemMeta() == null) return;

        if(inventory != null) {
            if(inventory.getTitle().equals(plugin.getInventoryManager().getInventory().getTitle())) {
                event.setCancelled(true);

                if(stack.getItemMeta().getDisplayName().contains("Solos") || stack.getItemMeta().getDisplayName().contains("Duos")) {
                    String name = stack.getItemMeta().getDisplayName();

                    plugin.getQueueManager().addToQueue(player, name.contains("Solos") ? true : false);
                }
            }
        }
    }
}
