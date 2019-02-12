package secondlife.network.practice.handlers;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import secondlife.network.practice.Practice;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.utilties.inventory.InventoryUI;

public class InventoryHandler implements Listener {

	private final Practice plugin = Practice.getInstance();

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (!player.getGameMode().equals(GameMode.CREATIVE)) {
			PracticeData playerData = PracticeData.getByName(player.getName());
			if (playerData.getPlayerState() == PlayerState.SPAWN || (playerData.getPlayerState() == PlayerState.EVENT && player.getItemInHand() != null && player.getItemInHand().getType() == Material.COMPASS)) {
				event.setCancelled(true);
			}
		}

		if(event.getClickedInventory() != null && event.getClickedInventory().getName() != null && event.getClickedInventory().getName().contains("Stats")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getInventory() == null) {
			return;
		}
		if (!(event.getInventory().getHolder() instanceof InventoryUI.InventoryUIHolder)) {
			return;
		}
		if (event.getCurrentItem() == null) {
			return;
		}

		InventoryUI.InventoryUIHolder inventoryUIHolder = (InventoryUI.InventoryUIHolder) event.getInventory().getHolder();

		event.setCancelled(true);

		if (event.getClickedInventory() == null || !event.getInventory().equals(event.getClickedInventory())) {
			return;
		}
		InventoryUI ui = inventoryUIHolder.getInventoryUI();
		InventoryUI.ClickableItem item = ui.getCurrentUI().getItem(event.getSlot());

		if (item == null) {
			return;
		}
		item.onClick(event);
	}
}
