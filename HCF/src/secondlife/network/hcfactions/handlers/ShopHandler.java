package secondlife.network.hcfactions.handlers;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class ShopHandler extends Handler implements Listener {

	private String[] lines = { Color.translate("&a[Shop]"), "", Color.translate("&aClick to buy")};
	private String[] error = { Color.translate("&c[Shop]"), "", Color.translate("&cError")};
	private ItemStack BLANK = new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) DyeColor.GRAY.getData()).name(" ").build();

	public ShopHandler(HCF plugin) {
		super(plugin);
		
		Bukkit.getPluginManager().registerEvents(this, this.getInstance());
	}
	
	public Inventory openMainInventory(Player player) {
		Inventory inv = Bukkit.createInventory(null, 9, "Welcome to Shop");
		
		for(int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, this.BLANK);
		}
		
		inv.setItem(2, this.getWeaponsItem());
		inv.setItem(4, this.getPotions());
		inv.setItem(6, this.getOther());
		
		player.openInventory(inv);
		
		return inv;
	}
	
	public Inventory openWeaponInventory(Player player) {
		Inventory inv = Bukkit.createInventory(null, 36, "Weapon Shop");
		
		for(int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, this.BLANK);
		}
		
		inv.setItem(10, this.getKitMapFire());
		inv.setItem(11, this.getKitMapSharpness());
		inv.setItem(12, this.getKitMapGod());
		inv.setItem(13, this.getKitMapSecondLife());
		inv.setItem(14, this.getKitMapBestFire());
		inv.setItem(15, this.getKitMapBow());
		inv.setItem(16, this.getKitMapFROD());
		
		inv.setItem(19, this.setPaper("&7Cost: &d1,600$"));
		inv.setItem(20, this.setPaper("&7Cost: &d2,400$"));
		inv.setItem(21, this.setPaper("&7Cost: &d3,200$"));
		inv.setItem(22, this.setPaper("&7Cost: &d6,400$"));
		inv.setItem(23, this.setPaper("&7Cost: &d16,000$"));
		inv.setItem(24, this.setPaper("&7Cost: &d3,200$"));
		inv.setItem(25, this.setPaper("&7Cost: &d1,600$"));
		
		//inv.setItem(22, this.getKitMapFROD());
		
		player.openInventory(inv);
		
		return inv;
	}
	
	public Inventory openPotionInventory(Player player) {
		Inventory inv = Bukkit.createInventory(null, 36, "Potion Shop");
		
		for(int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, this.BLANK);
		}
		
		inv.setItem(10, this.getRegeneration());
		inv.setItem(11, this.getStrength2());
		inv.setItem(12, this.getInvisibillity());
		
		inv.setItem(14, this.getSlowness());
		inv.setItem(15, this.getPoison());
		inv.setItem(16, this.getHarming());
		
		inv.setItem(19, this.setPaper("&7Cost: &d800$"));
		inv.setItem(20, this.setPaper("&7Cost: &d1,000$"));
		inv.setItem(21, this.setPaper("&7Cost: &d800$"));

		inv.setItem(23, this.setPaper("&7Cost: &d300$"));
		inv.setItem(24, this.setPaper("&7Cost: &d300$"));
		inv.setItem(25, this.setPaper("&7Cost: &d300$"));
		
		player.openInventory(inv);
		
		return inv;
	}
	
	public Inventory openOtherInventory(Player player) {
		Inventory inv = Bukkit.createInventory(null, 36, "Other Shop");
		
		for(int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, this.BLANK);
		}
		
		inv.setItem(12, this.getSuperGapple());
		inv.setItem(14, this.getGapple());
		
		inv.setItem(21, this.setPaper("&7Cost: &d800$"));
		inv.setItem(23, this.setPaper("&7Cost: &d300$"));
		
		player.openInventory(inv);
		
		return inv;
	}
	
	@EventHandler
	public void onSignPlace(SignChangeEvent event) {
		if(event.getLine(0).equals("[Shop]")) {
			Player player = event.getPlayer();
			if (player.hasPermission("secondlife.op")) {
				for(int i = 0; i < this.lines.length; i++) {
					event.setLine(i, this.lines[i]);
				}
			} else {
				for(int i = 0; i < this.error.length; i++) {
					event.setLine(i, this.error[i]);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if((event.useInteractedBlock() == Event.Result.ALLOW) && ((block.getState() instanceof Sign))) {
			Sign sign = (Sign) block.getState();
			for(int i = 0; i < this.lines.length; i++) {
				if(!sign.getLine(i).equals(this.lines[i])) return;
			}
			
			this.openMainInventory(player);
		}
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		
		HCFData data = HCFData.getByName(player.getName());
		
        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !event.getCurrentItem().hasItemMeta()) return;
        if(event.getCurrentItem().getItemMeta() == null) return;
        
        if(event.getClickedInventory().getTitle().equalsIgnoreCase(Color.translate("Welcome to Shop"))) {
        	event.setCancelled(true);
        	
        	if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&6&lWeapon Inventory"))) {
        		event.setCancelled(true);
        		
        		this.openWeaponInventory(player);
        	} else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&6&lPotion Inventory"))) {
        		event.setCancelled(true);
        		
        		this.openPotionInventory(player);
           	} else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&6&lOther Inventory"))) {
        		event.setCancelled(true);
        		
        		this.openOtherInventory(player);
        	}
        } else if(event.getClickedInventory().getTitle().equalsIgnoreCase(Color.translate("Weapon Shop"))) {
        	event.setCancelled(true);
        	
        	if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&7[&b&lKitMap Fire&7]"))) {
        		event.setCancelled(true);
        		    		
        		if(1600 > data.getBalance()) {
					player.sendMessage(Color.translate("&cYou can't afford this!"));
					player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
        		} else {
    				data.setBalance(data.getBalance() - 1600);
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
					player.sendMessage(Color.translate("&aSuccessful purchase!"));
					
		      		if(player.getInventory().firstEmpty() == -1) { 
						player.sendMessage(Color.translate("&cYour inventory is full!"));
					} else {
						player.getInventory().addItem(this.getKitMapFire());
						player.updateInventory();
					}
        		}
        	} else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&7[&b&lKitMap Sharpness&7]"))) {
        		event.setCancelled(true);
	    		
        		if(2400 > data.getBalance()) {
					player.sendMessage(Color.translate("&cYou can't afford this!"));
					player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
        		} else {
    				data.setBalance(data.getBalance() - 2400);
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
					player.sendMessage(Color.translate("&aSuccessful purchase!"));
					
		      		if(player.getInventory().firstEmpty() == -1) { 
						player.sendMessage(Color.translate("&cYour inventory is full!"));
					} else {
						player.getInventory().addItem(this.getKitMapSharpness());
						player.updateInventory();
					}
        		}
        	} else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&7[&b&lKitMap God&7]"))) {
        		event.setCancelled(true);
	    		
        		if(3200 > data.getBalance()) {
					player.sendMessage(Color.translate("&cYou can't afford this!"));
					player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
        		} else {
    				data.setBalance(data.getBalance() - 3200);
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
					player.sendMessage(Color.translate("&aSuccessful purchase!"));
					
		      		if(player.getInventory().firstEmpty() == -1) { 
						player.sendMessage(Color.translate("&cYour inventory is full!"));
					} else {
						player.getInventory().addItem(this.getKitMapGod());
						player.updateInventory();
					}
        		}
        	} else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&7[&b&lSecondLife&7]"))) {
        		event.setCancelled(true);
	    		
        		if(6400 > data.getBalance()) {
					player.sendMessage(Color.translate("&cYou can't afford this!"));
					player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
        		} else {
    				data.setBalance(data.getBalance() - 6400);
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
					player.sendMessage(Color.translate("&aSuccessful purchase!"));
					
		      		if(player.getInventory().firstEmpty() == -1) { 
						player.sendMessage(Color.translate("&cYour inventory is full!"));
					} else {
						player.getInventory().addItem(this.getKitMapSecondLife());
						player.updateInventory();
					}
        		}
         	} else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&7[&b&l&k!&7]"))) {
        		event.setCancelled(true);
	    		
        		if(16000 > data.getBalance()) {
					player.sendMessage(Color.translate("&cYou can't afford this!"));
					player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
        		} else {
    				data.setBalance(data.getBalance() - 16000);
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
					player.sendMessage(Color.translate("&aSuccessful purchase!"));
					
		      		if(player.getInventory().firstEmpty() == -1) { 
						player.sendMessage(Color.translate("&cYour inventory is full!"));
					} else {
						player.getInventory().addItem(this.getKitMapBestFire());
						player.updateInventory();
					}
        		}
         	} else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&7[&b&lKitMap Bow&7]"))) {
        		event.setCancelled(true);
	    		
        		if(3200 > data.getBalance()) {
					player.sendMessage(Color.translate("&cYou can't afford this!"));
					player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
        		} else {
    				data.setBalance(data.getBalance() - 3200);
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
					player.sendMessage(Color.translate("&aSuccessful purchase!"));
					
		      		if(player.getInventory().firstEmpty() == -1) { 
						player.sendMessage(Color.translate("&cYour inventory is full!"));
					} else {
						player.getInventory().addItem(this.getKitMapBow());
						player.updateInventory();
					}
        		}
         	} else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&7[&b&lKitMap Knockback&7]"))) {
        		event.setCancelled(true);
	    		
        		if(2400 > data.getBalance()) {
					player.sendMessage(Color.translate("&cYou can't afford this!"));
					player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
        		} else {
    				data.setBalance(data.getBalance() - 2400);
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
					player.sendMessage(Color.translate("&aSuccessful purchase!"));
					
		      		if(player.getInventory().firstEmpty() == -1) { 
						player.sendMessage(Color.translate("&cYour inventory is full!"));
					} else {
						player.getInventory().addItem(this.getKitMapKnockback());
						player.updateInventory();
					}
        		}
         	} else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&7[&b&lKitMap Rod&7]"))) {
        		event.setCancelled(true);
	    		
        		if(1600 > data.getBalance()) {
					player.sendMessage(Color.translate("&cYou can't afford this!"));
					player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
        		} else {
    				data.setBalance(data.getBalance() - 1600);
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
					player.sendMessage(Color.translate("&aSuccessful purchase!"));
					
		      		if(player.getInventory().firstEmpty() == -1) { 
						player.sendMessage(Color.translate("&cYour inventory is full!"));
					} else {
						player.getInventory().addItem(this.getKitMapFROD());
						player.updateInventory();
					}
        		}
        	}
        } else if(event.getClickedInventory().getTitle().equalsIgnoreCase(Color.translate("Potion Shop"))) {
        	event.setCancelled(true);
        	
        	 if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&7[&b&lKitMap Strength&7]"))) {
         		event.setCancelled(true);
 	    		
         		if(1000 > data.getBalance()) {
 					player.sendMessage(Color.translate("&cYou can't afford this!"));
 					player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
         		} else {
     				data.setBalance(data.getBalance() - 1000);
 					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
 					player.sendMessage(Color.translate("&aSuccessful purchase!"));
 					
 		      		if(player.getInventory().firstEmpty() == -1) { 
 						player.sendMessage(Color.translate("&cYour inventory is full!"));
 					} else {
 						player.getInventory().addItem(this.getStrength2());
 						player.updateInventory();
 					}
         		}
         	} else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&7[&b&lKitMap Invisibillity&7]"))) {
         		event.setCancelled(true);
 	    		
         		if(800 > data.getBalance()) {
 					player.sendMessage(Color.translate("&cYou can't afford this!"));
 					player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
         		} else {
     				data.setBalance(data.getBalance() - 800);
 					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
 					player.sendMessage(Color.translate("&aSuccessful purchase!"));
 					
 		      		if(player.getInventory().firstEmpty() == -1) { 
 						player.sendMessage(Color.translate("&cYour inventory is full!"));
 					} else {
 						player.getInventory().addItem(this.getInvisibillity());
 						player.updateInventory();
 					}
         		}
         	} else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&7[&b&lKitMap Regeneration&7]"))) {
         		event.setCancelled(true);
 	    		
         		if(800 > data.getBalance()) {
 					player.sendMessage(Color.translate("&cYou can't afford this!"));
 					player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
         		} else {
     				data.setBalance(data.getBalance() - 800);
 					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
 					player.sendMessage(Color.translate("&aSuccessful purchase!"));
 					
 		      		if(player.getInventory().firstEmpty() == -1) { 
 						player.sendMessage(Color.translate("&cYour inventory is full!"));
 					} else {
 						player.getInventory().addItem(this.getRegeneration());
 						player.updateInventory();
 					}
         		}
         	} else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&7[&b&lKitMap Slowness&7]"))) {
         		event.setCancelled(true);
 	    		
         		if(300 > data.getBalance()) {
 					player.sendMessage(Color.translate("&cYou can't afford this!"));
 					player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
         		} else {
     				data.setBalance(data.getBalance() - 300);
 					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
 					player.sendMessage(Color.translate("&aSuccessful purchase!"));
 					
 		      		if(player.getInventory().firstEmpty() == -1) { 
 						player.sendMessage(Color.translate("&cYour inventory is full!"));
 					} else {
 						player.getInventory().addItem(this.getSlowness());
 						player.updateInventory();
 					}
         		}
         	} else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&7[&b&lKitMap Poison&7]"))) {
         		event.setCancelled(true);
 	    		
				if(300 > data.getBalance()) {
 					player.sendMessage(Color.translate("&cYou can't afford this!"));
 					player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
         		} else {
     				data.setBalance(data.getBalance() - 300);
 					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
 					player.sendMessage(Color.translate("&aSuccessful purchase!"));
 					
 		      		if(player.getInventory().firstEmpty() == -1) { 
 						player.sendMessage(Color.translate("&cYour inventory is full!"));
 					} else {
 						player.getInventory().addItem(this.getPoison());
 						player.updateInventory();
 					}
         		}
         	} else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&7[&b&lKitMap Harming&7]"))) {
         		event.setCancelled(true);
 	    		
				if(300 > data.getBalance()) {
 					player.sendMessage(Color.translate("&cYou can't afford this!"));
 					player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
         		} else {
     				data.setBalance(data.getBalance() - 300);
 					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
 					player.sendMessage(Color.translate("&aSuccessful purchase!"));
 					
 		      		if(player.getInventory().firstEmpty() == -1) { 
 						player.sendMessage(Color.translate("&cYour inventory is full!"));
 					} else {
 						player.getInventory().addItem(this.getHarming());
 						player.updateInventory();
 					}
         		}
         	}
        } else if(event.getClickedInventory().getTitle().equalsIgnoreCase(Color.translate("Other Shop"))) {
        	event.setCancelled(true);
        	
			if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&7[&b&lKitMap Gapple&7]"))) {
				event.setCancelled(true);

				if(300 > data.getBalance()) {
					player.sendMessage(Color.translate("&cYou can't afford this!"));
					player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
				} else {
					data.setBalance(data.getBalance() - 300);
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
					player.sendMessage(Color.translate("&aSuccessful purchase!"));

					if(player.getInventory().firstEmpty() == -1) {
						player.sendMessage(Color.translate("&cYour inventory is full!"));
					} else {
						player.getInventory().addItem(this.getGapple());
						player.updateInventory();
					}
				}
			} else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&7[&b&lKitMap SGapple&7]"))) {
				event.setCancelled(true);

				if(800 > data.getBalance()) {
					player.sendMessage(Color.translate("&cYou can't afford this!"));
					player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
				} else {
					data.setBalance(data.getBalance() - 800);
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
					player.sendMessage(Color.translate("&aSuccessful purchase!"));

					if(player.getInventory().firstEmpty() == -1) {
						player.sendMessage(Color.translate("&cYour inventory is full!"));
					} else {
						player.getInventory().addItem(this.getSuperGapple());
						player.updateInventory();
					}
				}
			}
		}
	}
	
	public ItemStack setPaper(String string) {
		ItemStack stack = new ItemStack(Material.PAPER);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate(string));
		
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public ItemStack getWeaponsItem() {
		ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&6&lWeapon Inventory"));
		
		List<String> lore = new ArrayList<String>();
		
		lore.add(Color.translate("&7Weapon items inventory."));

		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public ItemStack getPotions() {
		ItemStack stack = new ItemStack(Material.POTION);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&6&lPotion Inventory"));
		
		List<String> lore = new ArrayList<String>();
		
		lore.add(Color.translate("&7Potion items inventory."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public ItemStack getOther() {
		ItemStack stack = new ItemStack(Material.GOLDEN_APPLE);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&6&lOther Inventory"));
		
		List<String> lore = new ArrayList<String>();
		
		lore.add(Color.translate("&7Other items inventory."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public ItemStack getKitMapFire() {
		ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&7[&b&lKitMap Fire&7]"));
		
		meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
		meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);

		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public ItemStack getKitMapSharpness() {
		ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&7[&b&lKitMap Sharpness&7]"));
		
		meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);

		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public ItemStack getKitMapGod() {
		ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&7[&b&lKitMap God&7]"));
		
		meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
		meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);

		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public ItemStack getKitMapSecondLife() {
		ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&7[&b&lSecondLife&7]"));
		
		meta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
		meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);

		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public ItemStack getKitMapBestFire() {
		ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&7[&b&l&k!&7]"));
		
		meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
		meta.addEnchant(Enchantment.FIRE_ASPECT, 5, true);

		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public ItemStack getKitMapBow() {
		ItemStack stack = new ItemStack(Material.BOW);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&7[&b&lKitMap Bow&7]"));
		
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 8, true);
		meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);

		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public ItemStack getKitMapKnockback() {
		ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&7[&b&lKitMap Knockback&7]"));
		
		meta.addEnchant(Enchantment.KNOCKBACK, 3, true);

		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public ItemStack getKitMapFROD() {
		ItemStack stack = new ItemStack(Material.FISHING_ROD);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&7[&b&lKitMap Rod&7]"));
		
		meta.addEnchant(Enchantment.DURABILITY, 3, true);

		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public ItemStack getStrength2() {
		ItemStack stack = new ItemStack(Material.POTION, 1, (short) 16425);
		ItemMeta meta = stack.getItemMeta();

		meta.setDisplayName(Color.translate("&7[&b&lKitMap Strength&7]"));

		stack.setItemMeta(meta);

		return stack;
	}

	public ItemStack getInvisibillity() {
		ItemStack stack = new ItemStack(Material.POTION, 1, (short) 16462);
		ItemMeta meta = stack.getItemMeta();

		meta.setDisplayName(Color.translate("&7[&b&lKitMap Invisibillity&7]"));

		stack.setItemMeta(meta);

		return stack;
	}

	public ItemStack getRegeneration() {
		ItemStack stack = new ItemStack(Material.POTION, 1, (short) 16449);
		ItemMeta meta = stack.getItemMeta();

		meta.setDisplayName(Color.translate("&7[&b&lKitMap Regeneration&7]"));

		stack.setItemMeta(meta);

		return stack;
	}

	public ItemStack getSlowness() {
		ItemStack stack = new ItemStack(Material.POTION, 1, (short) 16426);
		ItemMeta meta = stack.getItemMeta();

		meta.setDisplayName(Color.translate("&7[&b&lKitMap Slowness&7]"));

		stack.setItemMeta(meta);

		return stack;
	}

	public ItemStack getPoison() {
		ItemStack stack = new ItemStack(Material.POTION, 1, (short) 16388);
		ItemMeta meta = stack.getItemMeta();

		meta.setDisplayName(Color.translate("&7[&b&lKitMap Poison&7]"));

		stack.setItemMeta(meta);

		return stack;
	}

	public ItemStack getHarming() {
		ItemStack stack = new ItemStack(Material.POTION, 1, (short) 16428);
		ItemMeta meta = stack.getItemMeta();

		meta.setDisplayName(Color.translate("&7[&b&lKitMap Harming&7]"));

		stack.setItemMeta(meta);

		return stack;
	}

	public ItemStack getSuperGapple() {
		ItemStack stack = new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1);
		ItemMeta meta = stack.getItemMeta();

		meta.setDisplayName(Color.translate("&7[&b&lKitMap SGapple&7]"));

		stack.setItemMeta(meta);

		return stack;
	}

	public ItemStack getGapple() {
		ItemStack stack = new ItemStack(Material.GOLDEN_APPLE, 3, (short) 0);
		ItemMeta meta = stack.getItemMeta();

		meta.setDisplayName(Color.translate("&7[&b&lKitMap Gapple&7]"));

		stack.setItemMeta(meta);

		return stack;
	}
}
