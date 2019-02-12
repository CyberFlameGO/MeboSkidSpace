package secondlife.network.hcfactions.staff.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.staff.StaffPlayerData;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class StaffModeHandler extends Handler implements Listener {
	
	private static HashMap<UUID, StaffPlayerData> staffModePlayers;
	
	public StaffModeHandler(HCF plugin) {
		super(plugin);
		
		staffModePlayers = new HashMap<UUID, StaffPlayerData>();
		
		Bukkit.getPluginManager().registerEvents(this, getInstance());
	}

	public static void disable() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			disableStaffMode(player);
		}
		
		staffModePlayers.clear();
	}
	
	public static void enableStaffMode(Player player) {
		StaffPlayerData staffData = new StaffPlayerData();
		
		staffData.setContents(player.getInventory().getContents());
		staffData.setArmor(player.getInventory().getArmorContents());
		staffData.setGameMode(player.getGameMode());
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
		
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
		
		player.setGameMode(GameMode.CREATIVE);
		
		player.getInventory().setItem(0, compass());
		player.getInventory().setItem(1, book());
		player.getInventory().setItem(3, axe());
		player.getInventory().setItem(4, button());
		player.getInventory().setItem(7, ice());
		player.getInventory().setItem(8, music());
				
		
		staffModePlayers.put(player.getUniqueId(), staffData);
	}
	
	public static void disableStaffMode(Player player) {
		if(staffModePlayers.containsKey(player.getUniqueId())) {
			StaffPlayerData staffData = staffModePlayers.get(player.getUniqueId());
			
			player.getInventory().setContents(staffData.getContents());
			player.getInventory().setArmorContents(staffData.getArmor());
			player.setGameMode(staffData.getGameMode());
			
			if(player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
				player.removePotionEffect(PotionEffectType.NIGHT_VISION);
			}
			
			staffModePlayers.remove(player.getUniqueId());
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		if(!staffModePlayers.containsKey(player.getUniqueId())) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		
		if(!staffModePlayers.containsKey(player.getUniqueId())) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();

		if(item == null || item.getType().equals(Material.AIR)) return;
		if(!item.hasItemMeta()) return;
		if(!item.getItemMeta().hasDisplayName()) return;
		if(!player.hasPermission(Permission.STAFF_PERMISSION)) return;

		if(isInStaffMode(player)) {
			if(!(event.getRightClicked() instanceof Player)) return;
			
			Player rightClicked = (Player) event.getRightClicked();
			
			if(item.getType() == Material.PACKED_ICE) {
				player.performCommand("freeze " + rightClicked.getName());
			} else if(item.getType() == Material.BOOK) {
				player.openInventory(this.createInventory(player, rightClicked));
				
				player.sendMessage(Color.translate("&eYou have opened inventory of &d" + rightClicked.getName() + "&e!"));
			}		
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		InventoryAction action = event.getAction();
		
		if(staffModePlayers.containsKey(player.getUniqueId())) {
			if(event.getSlotType().equals(SlotType.OUTSIDE)) {
				event.setCancelled(true);
				return;
			}
			
			if(item == null || item.getType().equals(Material.AIR)) {
				if(action.equals(InventoryAction.HOTBAR_SWAP) || action.equals(InventoryAction.SWAP_WITH_CURSOR)) {
					event.setCancelled(true);
					return;
				}
			}
			event.setCancelled(true);
			
			if(item.hasItemMeta()) {	
				if(item.getItemMeta().hasDisplayName()) {
					if(item.getItemMeta().getDisplayName().equals(ChatColor.RED + "Close Preview")) {
						new BukkitRunnable() {
							@Override
							public void run() {
								player.closeInventory();	
							}
						}.runTaskLater(HCF.getInstance(), 1L);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack item = event.getItem();
		
		if(item == null || item.getType().equals(Material.AIR)) return;
		if(!item.hasItemMeta()) return;
		if(!item.getItemMeta().hasDisplayName()) return;
		if(!player.hasPermission(Permission.STAFF_PERMISSION)) return;
		
		if(staffModePlayers.containsKey(player.getUniqueId())) {
			if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
				
				if(item.getType() == Material.WATCH) {
					randomTeleport(player);
				} else if(item.getType() == Material.CHEST) {
					player.performCommand("onlinestaff");
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		
		if(!staffModePlayers.containsKey(player.getUniqueId())) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		
		if(!staffModePlayers.containsKey(player.getUniqueId())) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		disableStaffMode(player);
	}
	
	public void randomTeleport(Player player) {
		ArrayList<Player> online = new ArrayList<Player>();
		for(Player players : Bukkit.getOnlinePlayers()) {
			if(players != player) {
				online.add(players);
			}
		}
		
		if(online.size() != 0) {
			Player target = online.get(new Random().nextInt(online.size()));
			player.teleport(target);
			player.sendMessage(Color.translate("&eRandomly teleported you to &d" + target.getName() + "&e."));
		}
	}
	
	public Inventory createInventory(Player player, Player target) {
		Inventory inv = Bukkit.createInventory(null, 54, "Inventory preview");
		
		ItemStack[] contents = target.getInventory().getContents();
		ItemStack[] armor = target.getInventory().getArmorContents();
		
		inv.setContents(contents);
		
		inv.setItem(45, armor[0]);
		inv.setItem(46, armor[1]);
		inv.setItem(47, armor[2]);
		inv.setItem(48, armor[3]);
		
		inv.setItem(36, createGlass(ChatColor.RED + "Inventory Preview"));
		inv.setItem(37, createGlass(ChatColor.RED + "Inventory Preview"));
		inv.setItem(38, createGlass(ChatColor.RED + "Inventory Preview"));
		inv.setItem(39, createGlass(ChatColor.RED + "Inventory Preview"));
		inv.setItem(40, createGlass(ChatColor.RED + "Inventory Preview"));
		inv.setItem(41, createGlass(ChatColor.RED + "Inventory Preview"));
		inv.setItem(42, createGlass(ChatColor.RED + "Inventory Preview"));
		inv.setItem(43, createGlass(ChatColor.RED + "Inventory Preview"));
		inv.setItem(44, createGlass(ChatColor.RED + "Inventory Preview"));
		inv.setItem(49, createGlass(ChatColor.RED + "Inventory Preview"));
		
		inv.setItem(50, createItem(Material.SPECKLED_MELON, ChatColor.RED + "Health", (int)((Damageable)target).getHealth()));
		inv.setItem(51, createItem(Material.GRILLED_PORK, ChatColor.RED + "Hunger", target.getFoodLevel()));
		inv.setItem(52, createSkull(target, ChatColor.GREEN + target.getName()));
		inv.setItem(53, createWool(ChatColor.RED + "Close Preview", 14));
				
		return inv;
	}
	
	public static ItemStack compass() {
		return new ItemBuilder(Material.COMPASS).name("&5Teleporter").build();
	}
	
	public static ItemStack book() {
		return new ItemBuilder(Material.BOOK).name("&6Inventory Inspect").build();
	}
	
	public static ItemStack axe() {
		return new ItemBuilder(Material.WOOD_AXE).name("&eWorldEdit Wand").build();
	}
	
	public static ItemStack button() {
		return new ItemBuilder(Material.STONE_BUTTON).durability(1).name("&bBetter View").build();
	}
	
	public static ItemStack ice() {
		return new ItemBuilder(Material.PACKED_ICE).durability(1).name("&3Freeze").build();
	}
	
	public static ItemStack music() {
		return new ItemBuilder(Material.WATCH).durability(1).name("&cRandom Teleport").build();
	}
	
	public static boolean isInStaffMode(Player player) {
		return staffModePlayers.containsKey(player.getUniqueId());
	}
	
	public ItemStack createItem(Material material, String name, int amount) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		item.setItemMeta(itemmeta);
		
		return item;
	}
	
	public ItemStack createGlass(String name) {
		ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		item.setItemMeta(itemmeta);
		
		return item;
	}
	
	public ItemStack createWool(String name, int value) {
		ItemStack item = new ItemStack(Material.WOOL, 1, (short) value);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(name);
		item.setItemMeta(itemmeta);
		return item;
	}
	
	public ItemStack createSkull(Player player, String name) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skullmeta = (SkullMeta) item.getItemMeta();
		skullmeta.setDisplayName(name);
		skullmeta.setOwner(player.getName());
		item.setItemMeta(skullmeta);
		
		return item;
	}
}
