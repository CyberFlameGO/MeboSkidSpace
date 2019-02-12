package secondlife.network.hcfactions.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.hcfactions.utilties.file.UtilitiesFile;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.inventory.InventoryUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MapKitHandler extends Handler implements Listener {

	public static Inventory mapKitInv;
	public static List<UUID> editingMapKit;
	
	public MapKitHandler(HCF plugin) {
		super(plugin);
		
		editingMapKit = new ArrayList<UUID>();
		
		loadInventory();
		
		Bukkit.getPluginManager().registerEvents(this, this.getInstance());
	}

	public static void disable() {
		saveInventory();
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inv = event.getInventory();

		if(!(event.getWhoClicked() instanceof Player)) return;
		
		Player player = (Player) event.getWhoClicked();
		
		if(!inv.getName().equals(mapKitInv.getName())) return;
		if(player.hasPermission(Permission.OP_PERMISSION)) return;
		if(editingMapKit.contains(player.getUniqueId())) return;
		
		event.setCancelled(true);

	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		HumanEntity entity = event.getPlayer();
		Inventory inv = event.getInventory();
		
		if(!(entity instanceof Player)) return;
		
		Player player = (Player) entity;

		if(!inv.getName().equals(mapKitInv.getName())) return;
		if(!editingMapKit.contains(player.getUniqueId())) return;
		
		editingMapKit.remove(player.getUniqueId());
		
		player.sendMessage(Color.translate("&eYou successfully edited &dMap Kit&e."));
	}
			
	public static void loadInventory() {
		mapKitInv = Bukkit.createInventory(null, 54, "Map Kit");
		String mapKitItems = UtilitiesFile.getString("map-kit-items");
		
		if(mapKitItems != null) {
			if(!mapKitItems.isEmpty()) {
				try {
					ItemStack[] contents = InventoryUtils.itemStackArrayFromBase64(UtilitiesFile.getString("map-kit-items"));
					
					mapKitInv.setContents(contents);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void saveInventory() {
		ItemStack[] contents = mapKitInv.getContents();
		String contentsBase64 = InventoryUtils.itemStackArrayToBase64(contents);
		
		UtilitiesFile.configuration.set("map-kit-items", contentsBase64);
		
		try {
			UtilitiesFile.configuration.save(UtilitiesFile.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
