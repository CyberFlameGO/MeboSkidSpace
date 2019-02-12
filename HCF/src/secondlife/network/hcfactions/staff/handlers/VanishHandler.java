package secondlife.network.hcfactions.staff.handlers;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.staff.OptionType;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class VanishHandler extends Handler implements Listener {
	
	public static ArrayList<UUID> vanishedPlayers;
	public static ArrayList<UUID> silentView;
	
	public VanishHandler(HCF plugin) {
		super(plugin);
		
		vanishedPlayers = new ArrayList<UUID>();
		silentView = new ArrayList<UUID>();
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public static void disable() {
		vanishedPlayers.clear();
		silentView.clear();
	}
	
	public static boolean isVanished(Player player) {
		return vanishedPlayers.contains(player.getUniqueId());
	}
	
	public static void vanishPlayer(Player player) {
		vanishedPlayers.add(player.getUniqueId());
		
		for(Player online : Bukkit.getOnlinePlayers()) {
			if(!online.hasPermission(Permission.STAFF_PERMISSION)) {
				online.hidePlayer(player);
			}
		}
		
		player.sendMessage(Color.translate("&eYou have &aEnabled&e Vanish!"));
	
		Msg.sendMessage(Color.translate("&ePlayer &d" + player.getName() + " &ehas Vanished! :O"), Permission.STAFF_PERMISSION);
	}
	
	public static void vanishOnJoin(Player player) {
		vanishedPlayers.add(player.getUniqueId());
		
		for(Player online : Bukkit.getOnlinePlayers()) {
			if(!online.hasPermission(Permission.STAFF_PERMISSION)) {
				online.hidePlayer(player);
			}
		}
	}
	
	public static void unvanishPlayer(Player player) {	
		vanishedPlayers.remove(player.getUniqueId());
		
		for(Player online : Bukkit.getOnlinePlayers()) {
			if(!online.canSee(player)) {
				online.showPlayer(player);
			}
		}
		
		player.sendMessage(Color.translate("&eYou have &cDisabled&e Vanish."));
		
		Msg.sendMessage(Color.translate("&ePlayer &d" + player.getName() + " &ehas become Visible!"), Permission.STAFF_PERMISSION);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onVehicleDestroy(VehicleDestroyEvent event) {
		OptionType interact = OptionType.INTERACT;
		
		if(!(event.getAttacker() instanceof Player)) return;
		if(!isVanished((Player) event.getAttacker())) return;
		if(interact.getPlayers().contains(((Player) event.getAttacker()).getUniqueId())) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		if(!isVanished((Player) event.getEntity())) return;
		
		event.setCancelled(true);
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if(!(event.getTarget() instanceof Player)) return;
		if(!isVanished((Player) event.getTarget())) return;
		
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		OptionType option = OptionType.PLACE;
		
		if(!vanishedPlayers.contains(player.getUniqueId())) return;
		if(option.getPlayers().contains(player.getUniqueId())) return;
		
		event.setCancelled(true);
		
		player.sendMessage(Color.translate("&cYou can't place blocks vanished."));
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		OptionType option = OptionType.BREAK;
		
		if(!vanishedPlayers.contains(player.getUniqueId())) return;
		if(option.getPlayers().contains(player.getUniqueId())) return;
		
		event.setCancelled(true);
		
		player.sendMessage(Color.translate("&cYou can't break blocks vanished."));
	}
	
	@EventHandler
	public void onPlayerItemPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		OptionType option = OptionType.PICKUP;

		if(!vanishedPlayers.contains(player.getUniqueId())) return;
		if(option.getPlayers().contains(player.getUniqueId())) return;
		
		event.setCancelled(true);		
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		OptionType chestOption = OptionType.CHEST;
		OptionType interact = OptionType.INTERACT;
		
		if(!vanishedPlayers.contains(player.getUniqueId())) return;
		if(chestOption.getPlayers().contains(player.getUniqueId())) return;
		
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block block = event.getClickedBlock();

			if(!block.getType().equals(Material.CHEST)) return;
			if(block.getType().equals(Material.TRAPPED_CHEST)) return;
			if(player.isSneaking()) return;
			
			event.setCancelled(true);

			silentView.add(player.getUniqueId());

			Chest chest = (Chest) block.getState();
			Inventory inventory = Bukkit.createInventory(null, chest.getInventory().getSize());

			inventory.setContents(chest.getInventory().getContents());
			player.openInventory(inventory);

			player.sendMessage(Color.translate("&4&l[Silent] &7Opening silently. Can not edit."));
		}
				
		if(interact.getPlayers().contains(player.getUniqueId())) return;
		
		if(event.getAction().equals(Action.PHYSICAL)) {
			event.setCancelled(true);
		}

		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block block = event.getClickedBlock();

			if(!block.getType().equals(Material.LEVER)) return;
			if(!block.getType().equals(Material.WOOD_BUTTON)) return;
			if(!block.getType().equals(Material.STONE_BUTTON)) return;
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		InventoryAction action = event.getAction();
		
		if(!silentView.contains(player.getUniqueId())) return;
		
		if(action.equals(InventoryAction.HOTBAR_SWAP) || action.equals(InventoryAction.SWAP_WITH_CURSOR)) {
			event.setCancelled(true);
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		
		if(!vanishedPlayers.contains(player.getUniqueId())) return;
		if(!silentView.contains(player.getUniqueId())) return;
		
		silentView.remove(player.getUniqueId());
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		OptionType option = OptionType.DAMAGE;

		if(!(event.getEntity() instanceof Player)) return;
		
		Player player = (Player) event.getEntity();
		
		if(!vanishedPlayers.contains(player.getUniqueId())) return;
		if(option.getPlayers().contains(player.getUniqueId())) return;
		
		event.setCancelled(true);
	}
	
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		OptionType option = OptionType.DAMAGE;
		
		if(!(event.getEntity() instanceof Player)) return;
		if(!(event.getDamager() instanceof Player)) return;
		
		Player damager = (Player) event.getDamager();
		if(!vanishedPlayers.contains(damager.getUniqueId())) return;
		if(option.getPlayers().contains(damager.getUniqueId())) return;
		
		event.setCancelled(true);
		
		damager.sendMessage(Color.translate("&cYou cannot deal damage while vanished."));
		
		if(!(event.getEntity() instanceof Villager)) return;
		
		Villager villager = (Villager) event.getEntity();
		
		if(!(event.getDamager() instanceof Player)) return;
		if(!vanishedPlayers.contains(damager.getUniqueId())) return;
		if(!villager.hasMetadata("CombatLogger")) return;
		
		event.setCancelled(true);
		
		damager.sendMessage(Color.translate("&cYou can't deal damage while vanished."));
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if(player.hasPermission("secondlife.staff")) {
			new BukkitRunnable() {
				public void run() {
					vanishOnJoin(player);
					
					player.sendMessage(Color.translate("&eYou have joined Vanished!"));
					for(Player online : Bukkit.getOnlinePlayers()) {
						if(online != player && online.hasPermission("secondlife.staff")) {
							online.sendMessage(Color.translate("&ePlayer &d" + player.getName() + " &ehas joined Vanished! :O"));
						}
					}
				}
			}.runTaskLater(this.getInstance(), 2L);
		}
		
		if(vanishedPlayers.size() != 0) {
			for(Player online : Bukkit.getOnlinePlayers()) {
				if(vanishedPlayers.contains(online.getUniqueId())) {
					if(!player.hasPermission("secondlife.staff")) {
						player.hidePlayer(online);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if(!vanishedPlayers.contains(player.getUniqueId())) return;
		
		vanishedPlayers.remove(player.getUniqueId());
	}
}
