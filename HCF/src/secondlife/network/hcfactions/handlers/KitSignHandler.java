package secondlife.network.hcfactions.handlers;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

public class KitSignHandler extends Handler implements Listener {
	
	public static ArrayList<String> cooldown;
	
	public static ItemStack healpot;
	public static ItemStack speedpot;
	public static ItemStack frespot;
	
	public KitSignHandler(HCF plugin) {
		super(plugin);
		
		cooldown = new ArrayList<>();
		healpot = new ItemStack(Material.POTION, 1, (short) 16421);
		speedpot = new ItemStack(Material.POTION, 1, (short) 8226);
		frespot = new ItemStack(Material.POTION, 1, (short) 8259);
		
		Bukkit.getPluginManager().registerEvents(this, this.getInstance());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			BlockState state = event.getClickedBlock().getState();

			if(state instanceof Sign) {
				Sign s = (Sign) state;

				if(cooldown.contains(player.getName())) {
					player.sendMessage(Color.translate("&cPlease wait before using this!"));
					return;
				}

				if(s.getLine(1).equals(Color.translate("&b&lKit")) && (s.getLine(2).equals(Color.translate("&b&lDiamond")))) {
					giveDiamondKit(player);
					
					cooldown.add(player.getName());
					new BukkitRunnable() {
						public void run() {
							cooldown.remove(player.getName());
						}
					}.runTaskLater(this.getInstance(), 100L);
				} else if(s.getLine(1).equals(Color.translate("&e&lKit")) && (s.getLine(2).equals(Color.translate("&e&lBard")))) {
					giveBardKit(player);
					
					cooldown.add(player.getName());
					new BukkitRunnable() {
						public void run() {
							cooldown.remove(player.getName());
						}
					}.runTaskLater(this.getInstance(), 100L);
				} else if(s.getLine(1).equals(Color.translate("&5&lKit")) && (s.getLine(2).equals(Color.translate("&5&lArcher")))) {
					giveArcherKit(player);
					
					cooldown.add(player.getName());
					new BukkitRunnable() {
						public void run() {
							cooldown.remove(player.getName());
						}
					}.runTaskLater(this.getInstance(), 100L);
				} else if(s.getLine(1).equals(Color.translate("&c&lKit")) && (s.getLine(2).equals(Color.translate("&c&lBuilder")))) {
					giveBuilderKit(player);
					
					cooldown.add(player.getName());
					new BukkitRunnable() {
						public void run() {
							cooldown.remove(player.getName());
						}
					}.runTaskLater(this.getInstance(), 100L);
				} else if(s.getLine(1).equals(Color.translate("&a&lKit")) && (s.getLine(2).equals(Color.translate("&a&lRogue")))) {
					giveRogueKit(player);
					
					cooldown.add(player.getName());
					new BukkitRunnable() {
						public void run() {
							cooldown.remove(player.getName());
						}
					}.runTaskLater(this.getInstance(), 100L);
				}
			}
		}
	}
	
	  public void giveDiamondKit(Player player) {
			ItemStack diamondhelmet = new ItemStack(Material.DIAMOND_HELMET, 1);
			diamondhelmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			diamondhelmet.addEnchantment(Enchantment.DURABILITY, 3);
			
			ItemStack diamondplate = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
			diamondplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			diamondplate.addEnchantment(Enchantment.DURABILITY, 3);
			
			ItemStack diamondleggs = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
			diamondleggs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			diamondleggs.addEnchantment(Enchantment.DURABILITY, 3);
			
			ItemStack diamondboots = new ItemStack(Material.DIAMOND_BOOTS, 1);
			diamondboots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			diamondboots.addEnchantment(Enchantment.DURABILITY, 3);
			diamondboots.addEnchantment(Enchantment.PROTECTION_FALL, 4);
			
			ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
			sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
			
			PlayerInventory pi = player.getInventory();
			// 4
		  boolean hasSword = false;

		  for(ItemStack item : player.getInventory().getContents()) {
			  if(item != null && item.getType() == Material.DIAMOND_SWORD) {
				  hasSword = true;
			  }
		  }

		  if(!hasSword) {
			  pi.setItem(0, sword);
		  }
		
			pi.setItem(2, frespot);
			pi.setItem(3, speedpot);

			pi.setItem(9, speedpot);

			pi.setItem(18, speedpot);
			pi.setItem(27, speedpot);

			pi.setItem(1, new ItemStack(Material.ENDER_PEARL, 16));
			//pi.setItem(2, this.frespot);
			//pi.setItem(3, this.speedpot);
			pi.setItem(4, healpot);
			pi.setItem(5, healpot);
			pi.setItem(6, healpot);
			pi.setItem(7, healpot);
			pi.setItem(8, new ItemStack(Material.BAKED_POTATO, 64));
			// 3
			//pi.setItem(9, this.speedpot);
			pi.setItem(10, healpot);
			pi.setItem(11, healpot);
			pi.setItem(12, healpot);
			pi.setItem(13, healpot);
			pi.setItem(14, healpot);
			pi.setItem(15, healpot);
			pi.setItem(16, healpot);
			pi.setItem(17, healpot);
			// 2
			//pi.setItem(18, this.speedpot);
			pi.setItem(19, healpot);
			pi.setItem(20, healpot);
			pi.setItem(21, healpot);
			pi.setItem(22, healpot);
			pi.setItem(23, healpot);
			pi.setItem(24, healpot);
			pi.setItem(25, healpot);
			pi.setItem(26, healpot);
			// 1
			//pi.setItem(27, this.speedpot);
			pi.setItem(28, healpot);
			pi.setItem(29, healpot);
			pi.setItem(30, healpot);
			pi.setItem(31, healpot);
			pi.setItem(32, healpot);
			pi.setItem(33, healpot);
			pi.setItem(34, healpot);
			pi.setItem(35, healpot);
			// ARMOR
		  pi.setHelmet(diamondhelmet);
		  pi.setChestplate(diamondplate);
		  pi.setLeggings(diamondleggs);
		  pi.setBoots(diamondboots);

			player.updateInventory();
			player.sendMessage(Color.translate(" &6" + Msg.KRUZIC + " &b&lEquipped Diamond Kit"));
		}
	    
	    public void giveRogueKit(Player player) {
			ItemStack diamondhelmet = new ItemStack(Material.CHAINMAIL_HELMET, 1);
			diamondhelmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			diamondhelmet.addEnchantment(Enchantment.DURABILITY, 3);
			
			ItemStack diamondplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
			diamondplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			diamondplate.addEnchantment(Enchantment.DURABILITY, 3);
			
			ItemStack diamondleggs = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1);
			diamondleggs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			diamondleggs.addEnchantment(Enchantment.DURABILITY, 3);
			
			ItemStack diamondboots = new ItemStack(Material.CHAINMAIL_BOOTS, 1);
			diamondboots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			diamondboots.addEnchantment(Enchantment.DURABILITY, 3);
			diamondboots.addEnchantment(Enchantment.PROTECTION_FALL, 4);
			
			ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
			sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
			
			ItemStack gold = new ItemStack(Material.GOLD_SWORD, 1);
			
			PlayerInventory pi = player.getInventory();
			// 4
			pi.setItem(1, new ItemStack(Material.ENDER_PEARL, 16));
			pi.setItem(2, gold);
			pi.setItem(3, gold);
			pi.setItem(4, gold);
			pi.setItem(5, frespot);
			pi.setItem(6, new ItemStack(Material.FEATHER, 64));
			pi.setItem(7, new ItemStack(Material.SUGAR, 64));
			pi.setItem(8, new ItemStack(Material.BAKED_POTATO, 64));
			// 3
			pi.setItem(9, gold);
			pi.setItem(10, gold);
			pi.setItem(11, gold);
			pi.setItem(12, healpot);
			pi.setItem(13, healpot);
			pi.setItem(14, healpot);
			pi.setItem(15, healpot);
			pi.setItem(16, healpot);
			pi.setItem(17, healpot);
			// 2
			pi.setItem(18, gold);
			pi.setItem(19, gold);
			pi.setItem(20, gold);
			pi.setItem(21, healpot);
			pi.setItem(22, healpot);
			pi.setItem(23, healpot);
			pi.setItem(24, healpot);
			pi.setItem(25, healpot);
			pi.setItem(26, healpot);
			// 1
			pi.setItem(27, healpot);
			pi.setItem(28, healpot);
			pi.setItem(29, healpot);
			pi.setItem(30, healpot);
			pi.setItem(31, healpot);
			pi.setItem(32, healpot);
			pi.setItem(33, healpot);
			pi.setItem(34, healpot);
			pi.setItem(35, healpot);
			// ARMOR

			boolean hasSword = false;

			for(ItemStack item : player.getInventory().getContents()) {
				if(item != null && item.getType() == Material.DIAMOND_SWORD) {
					hasSword = true;
				}
			}

			if(!hasSword) {
				pi.setItem(0, sword);
			}

			player.getInventory().setHelmet(diamondhelmet);
			player.getInventory().setChestplate(diamondplate);
			player.getInventory().setLeggings(diamondleggs);
			player.getInventory().setBoots(diamondboots);

			player.updateInventory();
			player.sendMessage(Color.translate(" &6" + Msg.KRUZIC + " &a&lEquipped Rogue Kit"));
		}

		
		public void giveBardKit(Player player) {
			ItemStack goldhelmet = new ItemStack(Material.GOLD_HELMET, 1);
			goldhelmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			goldhelmet.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
			
			ItemStack goldplate = new ItemStack(Material.GOLD_CHESTPLATE, 1);
			goldplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			goldplate.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
			
			ItemStack goldleggs = new ItemStack(Material.GOLD_LEGGINGS, 1);
			goldleggs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			goldleggs.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
			
			ItemStack goldboots = new ItemStack(Material.GOLD_BOOTS, 1);
			goldboots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			goldboots.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
			goldboots.addEnchantment(Enchantment.PROTECTION_FALL, 4);
			
			ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
			sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
			
			PlayerInventory pi = player.getInventory();
			// 4
			pi.setItem(1, new ItemStack(Material.ENDER_PEARL, 16));
			pi.setItem(2, new ItemStack(Material.BLAZE_POWDER, 32));
			pi.setItem(3, new ItemStack(Material.SUGAR, 32));
			pi.setItem(4, healpot);
			pi.setItem(5, healpot);
			pi.setItem(6, healpot);
			pi.setItem(7, frespot);
			pi.setItem(8, new ItemStack(Material.BAKED_POTATO, 64));
			// 3
			pi.setItem(9, new ItemStack(Material.IRON_INGOT, 32));
			pi.setItem(10, new ItemStack(Material.GHAST_TEAR, 32));
			pi.setItem(11, healpot);
			pi.setItem(12, healpot);
			pi.setItem(13, healpot);
			pi.setItem(14, healpot);
			pi.setItem(15, healpot);
			pi.setItem(16, healpot);
			pi.setItem(17, healpot);
			// 2
			pi.setItem(18, new ItemStack(Material.FEATHER, 32));
			pi.setItem(19, new ItemStack(Material.MAGMA_CREAM, 32));
			pi.setItem(20, healpot);
			pi.setItem(21, healpot);
			pi.setItem(22, healpot);
			pi.setItem(23, healpot);
			pi.setItem(24, healpot);
			pi.setItem(25, healpot);
			pi.setItem(26, healpot);
			// 1
			pi.setItem(27, healpot);
			pi.setItem(28, healpot);
			pi.setItem(29, healpot);
			pi.setItem(30, healpot);
			pi.setItem(31, healpot);
			pi.setItem(32, healpot);
			pi.setItem(33, healpot);
			pi.setItem(34, healpot);
			pi.setItem(35, healpot);
			// ARMOR
			boolean hasSword = false;

			for(ItemStack item : player.getInventory().getContents()) {
				if(item != null &&  item.getType() == Material.DIAMOND_SWORD) {
					hasSword = true;
				}
			}

			if(!hasSword) {
				pi.setItem(0, sword);
			}

			pi.setHelmet(goldhelmet);
			pi.setChestplate(goldplate);
			pi.setLeggings(goldleggs);
			pi.setBoots(goldboots);
			player.updateInventory();
			player.sendMessage(Color.translate(" &6" + Msg.KRUZIC + " &e&lEquipped Bard Kit"));
		}
		
		public void giveArcherKit(Player player) {
			ItemStack lhelmet = new ItemStack(Material.LEATHER_HELMET, 1);
			lhelmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			lhelmet.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
			
			ItemStack lplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
			lplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			lplate.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
			
			ItemStack lleggs = new ItemStack(Material.LEATHER_LEGGINGS, 1);
			lleggs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			lleggs.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
			
			ItemStack lboots = new ItemStack(Material.LEATHER_BOOTS, 1);
			lboots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			lboots.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
			lboots.addEnchantment(Enchantment.PROTECTION_FALL, 4);
			
			ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
			sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
			
			ItemStack bow = new ItemStack(Material.BOW, 1);
			bow.addEnchantment(Enchantment.ARROW_DAMAGE, 5);
			bow.addEnchantment(Enchantment.ARROW_FIRE, 1);
			bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
			bow.addEnchantment(Enchantment.DURABILITY, 3);
			
			PlayerInventory pi = player.getInventory();
			// 4
			pi.setItem(1, new ItemStack(Material.ENDER_PEARL, 16));
			pi.setItem(2, bow);
			pi.setItem(3, frespot);
			pi.setItem(4, healpot);
			pi.setItem(5, healpot);
			pi.setItem(6, healpot);
			pi.setItem(7, new ItemStack(Material.SUGAR, 32));
			pi.setItem(8, new ItemStack(Material.BAKED_POTATO, 64));
			// 3
			pi.setItem(9, new ItemStack(Material.ARROW, 1));
			pi.setItem(10, healpot);
			pi.setItem(11, healpot);
			pi.setItem(12, healpot);
			pi.setItem(13, healpot);
			pi.setItem(14, healpot);
			pi.setItem(15, healpot);
			pi.setItem(16, healpot);
			pi.setItem(17, healpot);
			// 2
			pi.setItem(18, healpot);
			pi.setItem(19, healpot);
			pi.setItem(20, healpot);
			pi.setItem(21, healpot);
			pi.setItem(22, healpot);
			pi.setItem(23, healpot);
			pi.setItem(24, healpot);
			pi.setItem(25, healpot);
			pi.setItem(26, healpot);
			// 1
			pi.setItem(27, healpot);
			pi.setItem(28, healpot);
			pi.setItem(29, healpot);
			pi.setItem(30, healpot);
			pi.setItem(31, healpot);
			pi.setItem(32, healpot);
			pi.setItem(33, healpot);
			pi.setItem(34, healpot);
			pi.setItem(35, healpot);
			// ARMOR
			boolean hasSword = false;

			for(ItemStack item : player.getInventory().getContents()) {
				if(item != null && item.getType() == Material.DIAMOND_SWORD) {
					hasSword = true;
				}
			}

			if(!hasSword) {
				pi.setItem(0, sword);
			}

			pi.setHelmet(lhelmet);
			pi.setChestplate(lplate);
			pi.setLeggings(lleggs);
			pi.setBoots(lboots);
			player.updateInventory();
			player.sendMessage(Color.translate(" &6" + Msg.KRUZIC + " &5&lEquipped Archer Kit"));
		}
		
	    public void giveBuilderKit(Player player) {
			ItemStack diamondhelmet = new ItemStack(Material.IRON_HELMET, 1);
			diamondhelmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			diamondhelmet.addEnchantment(Enchantment.DURABILITY, 3);
			
			ItemStack diamondplate = new ItemStack(Material.IRON_CHESTPLATE, 1);
			diamondplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			diamondplate.addEnchantment(Enchantment.DURABILITY, 3);
			
			ItemStack diamondleggs = new ItemStack(Material.IRON_LEGGINGS, 1);
			diamondleggs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			diamondleggs.addEnchantment(Enchantment.DURABILITY, 3);
			
			ItemStack diamondboots = new ItemStack(Material.IRON_BOOTS, 1);
			diamondboots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			diamondboots.addEnchantment(Enchantment.DURABILITY, 3);
			diamondboots.addEnchantment(Enchantment.PROTECTION_FALL, 4);
			
			ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
			sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
			
			ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE,1 );
			pick.addUnsafeEnchantment(Enchantment.DIG_SPEED, 10);
			pick.addEnchantment(Enchantment.DURABILITY, 3);
			
			ItemStack showel = new ItemStack(Material.DIAMOND_SPADE,1 );
			showel.addUnsafeEnchantment(Enchantment.DIG_SPEED, 10);
			showel.addEnchantment(Enchantment.DURABILITY, 3);
			
			ItemStack axe = new ItemStack(Material.DIAMOND_AXE,1 );
			axe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 10);
			axe.addEnchantment(Enchantment.DURABILITY, 3);
			
			PlayerInventory pi = player.getInventory();
			// 1
			pi.setItem(1, new ItemStack(Material.ENDER_PEARL, 16));
			pi.setItem(2, pick);
			pi.setItem(3, axe);
			pi.setItem(4, showel);
			pi.setItem(5, healpot);
			pi.setItem(6, speedpot);
			pi.setItem(7, frespot);
			pi.setItem(8, new ItemStack(Material.BAKED_POTATO, 64));
			// 2
			pi.setItem(9, speedpot);
			pi.setItem(10, new ItemStack(Material.STONE, 64));
			pi.setItem(11, new ItemStack(Material.COBBLESTONE, 64));
			pi.setItem(12, new ItemStack(Material.SMOOTH_BRICK, 64));
			pi.setItem(13, new ItemStack(Material.QUARTZ_BLOCK, 64));
			pi.setItem(14, new ItemStack(Material.DIRT, 64));
			pi.setItem(15, new ItemStack(Material.LOG, 64));
			pi.setItem(16, new ItemStack(Material.GLOWSTONE, 64));
			pi.setItem(17, new ItemStack(Material.WATER_BUCKET, 1));
			// 3
			pi.setItem(18, speedpot);
			pi.setItem(19, new ItemStack(Material.GLASS, 64));
			pi.setItem(20, new ItemStack(Material.PISTON_STICKY_BASE, 32));
			pi.setItem(21, new ItemStack(Material.PISTON_BASE, 32));
			pi.setItem(22, new ItemStack(Material.REDSTONE_COMPARATOR, 10));
			pi.setItem(23, new ItemStack(Material.DIODE, 32));
			pi.setItem(24, new ItemStack(Material.HOPPER, 64));
			pi.setItem(25, new ItemStack(Material.REDSTONE, 64));
			pi.setItem(26, new ItemStack(Material.REDSTONE, 64));
			// 4
			pi.setItem(27, speedpot);
			pi.setItem(28, new ItemStack(Material.STRING, 64));
			pi.setItem(29, new ItemStack(Material.STRING, 64));
			pi.setItem(30, new ItemStack(Material.FENCE_GATE, 64));
			pi.setItem(31, new ItemStack(Material.WOOD_PLATE, 64));
			pi.setItem(32, healpot);
			pi.setItem(33, healpot);
			pi.setItem(34, healpot);
			pi.setItem(35, healpot);
			// ARMOR
			boolean hasSword = false;

			for(ItemStack item : player.getInventory().getContents()) {
				if(item != null && item.getType() == Material.DIAMOND_SWORD) {
					hasSword = true;
				}
			}

			if(!hasSword) {
				pi.setItem(0, sword);
			}

			pi.setHelmet(diamondhelmet);
			pi.setChestplate(diamondplate);
			pi.setLeggings(diamondleggs);
			pi.setBoots(diamondboots);
			player.updateInventory();
			player.sendMessage(Color.translate(" &6" + Msg.KRUZIC + " &c&lEquipped Builder Kit"));
		}
}
