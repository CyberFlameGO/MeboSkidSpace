package secondlife.network.hub.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import secondlife.network.hub.Hub;
import secondlife.network.hub.data.StaffData;
import secondlife.network.overpass.utilties.events.LoginEvent;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.PlayerUtils;
import secondlife.network.vituz.utilties.Tasks;

/**
 * Created by Marko on 28.03.2018.
 */
public class DataStaffListener implements Listener {

	private Hub plugin = Hub.getInstance();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(!Vituz.getInstance().getDatabaseManager().isConnected()) {
			event.getPlayer().kickPlayer(Color.translate("&cServer is setting up..."));
			return;
		}

		Player player = event.getPlayer();

		if(!player.hasPermission(Permission.STAFF_PERMISSION)) {
			return;
		}

		StaffData data = StaffData.getByName(player.getName());

		if(!data.isLoaded()) {
			data.load();
		}

		if(!data.isLoaded()) {
			PlayerUtils.kick(event);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		StaffData.getByName(event.getPlayer().getName()).save();

		plugin.getStaffSecurityManager().handleRemove(event.getPlayer());
		event.getPlayer().getActivePotionEffects().forEach(effect -> event.getPlayer().removePotionEffect(effect.getType()));
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();

		if(!plugin.getStaffSecurityManager().getUsers().contains(player.getUniqueId())) return;

		event.setCancelled(true);

		StaffData data = StaffData.getByName(player.getName());

		if(data.getPassword().equalsIgnoreCase("")) {
			player.sendMessage(Color.translate("&cPlease register using /securityregister <password>"));
		} else {
			player.sendMessage(Color.translate("&cPlease login using /auth <password>"));
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		if(!plugin.getStaffSecurityManager().getUsers().contains(player.getUniqueId())) return;

		event.setCancelled(true);

		StaffData data = StaffData.getByName(player.getName());

		if(data.getPassword().equalsIgnoreCase("")) {
			player.sendMessage(Color.translate("&cPlease register using /securityregister <password>"));
		} else {
			player.sendMessage(Color.translate("&cPlease login using /auth <password>"));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		if(!plugin.getStaffSecurityManager().getUsers().contains(player.getUniqueId())) return;

		event.setCancelled(true);

		StaffData data = StaffData.getByName(player.getName());

		if(data.getPassword().equalsIgnoreCase("")) {
			player.sendMessage(Color.translate("&cPlease register using /securityregister <password>"));
		} else {
			player.sendMessage(Color.translate("&cPlease login using /auth <password>"));
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if(!plugin.getStaffSecurityManager().getUsers().contains(player.getUniqueId())) return;

		event.setCancelled(true);

		StaffData data = StaffData.getByName(player.getName());

		if(data.getPassword().equalsIgnoreCase("")) {
			player.sendMessage(Color.translate("&cPlease register using /securityregister <password>"));
		} else {
			player.sendMessage(Color.translate("&cPlease login using /auth <password>"));
		}
	}


	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		if(!plugin.getStaffSecurityManager().getUsers().contains(player.getUniqueId())) return;

		event.setCancelled(true);

		StaffData data = StaffData.getByName(player.getName());

		if(data.getPassword().equalsIgnoreCase("")) {
			player.sendMessage(Color.translate("&cPlease register using /securityregister <password>"));
		} else {
			player.sendMessage(Color.translate("&cPlease login using /auth <password>"));
		}
	}

	@EventHandler
	public void onLogin(LoginEvent event) {
		Player player = event.getPlayer();

		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 999));
		player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Integer.MAX_VALUE, 999));
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 999));

		player.closeInventory();
		plugin.getStaffSecurityManager().getUsers().add(player.getUniqueId());

		Tasks.runLater(() -> {
			if(!player.isOnline()) {
				return;
			}

			if(plugin.getStaffSecurityManager().getUsers().contains(player.getUniqueId())) {
				player.kickPlayer(Color.translate("&cLogin time exceeded!"));
			}
		}, 2400L);
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();

		if(!plugin.getStaffSecurityManager().getUsers().contains(player.getUniqueId())) return;

		if(event.getMessage().toLowerCase().startsWith("/code") || event.getMessage().toLowerCase().startsWith("/l ") || event.getMessage().toLowerCase().startsWith("/login") || event.getMessage().toLowerCase().startsWith("/auth") || event.getMessage().toLowerCase().startsWith("/security") || event.getMessage().toLowerCase().startsWith("/securityregister")) return;

		event.setCancelled(true);

		StaffData data = StaffData.getByName(player.getName());

		if(data.getPassword().equalsIgnoreCase("")) {
			player.sendMessage(Color.translate("&cPlease register using /securityregister <password>"));
		} else {
			player.sendMessage(Color.translate("&cPlease login using /auth <password>"));
		}
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		Player player = (Player) event.getPlayer();

		if(player == null) return;
		if(!plugin.getStaffSecurityManager().getUsers().contains(player.getUniqueId())) return;

		event.setCancelled(true);

		StaffData data = StaffData.getByName(player.getName());

		if(data.getPassword().equalsIgnoreCase("")) {
			player.sendMessage(Color.translate("&cPlease register using /securityregister <password>"));
		} else {
			player.sendMessage(Color.translate("&cPlease login using /auth <password>"));
		}
	}
}