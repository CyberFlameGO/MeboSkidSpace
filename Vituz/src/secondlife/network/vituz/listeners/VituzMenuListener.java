package secondlife.network.vituz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import secondlife.network.vituz.utilties.inventory.VituzMenu;

public class VituzMenuListener implements Listener {

	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        VituzMenu menu = VituzMenu.getByPlayer(player);

        if(menu == null) return;

        event.setCancelled(true);

        if(event.getClickedInventory() != null && event.getClickedInventory().getTitle().equals(menu.getInventory().getTitle())) {
            menu.onClickItem(player, event.getCurrentItem(), event.isRightClick());

            if(menu.getPlayer() != null && menu.getInventory() != null) {
                menu.updateInventory(player);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        VituzMenu menu = VituzMenu.getByPlayer(player);

        if(menu == null) return;

        if(event.getInventory().getTitle().equals(menu.getInventory().getTitle())) {
            menu.onClose();
        }

        menu.destroy();
    }
}