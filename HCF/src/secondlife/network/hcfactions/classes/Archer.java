package secondlife.network.hcfactions.classes;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.classes.utils.ArmorClass;
import secondlife.network.hcfactions.classes.utils.ArmorClassHandler;
import secondlife.network.hcfactions.classes.utils.bard.EffectRestorerHandler;
import secondlife.network.hcfactions.timers.ArcherHandler.TaggedTask;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.utilties.Color;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Archer extends ArmorClass implements Listener {
	
	public static TObjectLongMap<UUID> speed_cooldowns = new TObjectLongHashMap<>();
	public static TObjectLongMap<UUID> jump_cooldowns = new TObjectLongHashMap<>();

	public static PotionEffect speed_effect = new PotionEffect(PotionEffectType.SPEED, 160, 3);
	public static PotionEffect jump_effect = new PotionEffect(PotionEffectType.JUMP, 160, 7);

	public static long speed_cooldown_delay = TimeUnit.SECONDS.toMillis(45L);
	public static long jump_cooldown_delay = TimeUnit.MINUTES.toMillis(1L);
	
	public Archer() {
		super("Archer", !HCFConfiguration.kitMap ? 3 : 1);
		
		passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
		passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if(event.isCancelled()) return;
		
		if(!(event.getEntity() instanceof Player)) return;
		if(!(event.getDamager() instanceof Arrow)) return;
					
		Arrow arrow = (Arrow) event.getDamager();
		ProjectileSource source = arrow.getShooter();

		if(!(source instanceof Player)) return;

		Player damaged = (Player) event.getEntity();
		Player shooter = (Player) source;

		ArmorClass equipped = ArmorClassHandler.getEquippedClass(shooter);

		if((equipped == null) || (!equipped.equals(this))) return;
		
		if((ArmorClassHandler.getEquippedClass(damaged) != null) && (ArmorClassHandler.getEquippedClass(damaged).equals(this))) return;

		new TaggedTask(damaged);
		
		double distance = shooter.getLocation().distance(damaged.getLocation());

		shooter.sendMessage(Color.translate("&e[&9Arrow Range &e(&c" + String.format("%.1f", Double.valueOf(distance)) + "&e)] " + "&6Marked " + damaged.getName() + " &6for 10 seconds."));
		damaged.sendMessage(Color.translate("&eYou were &dArcher Tagged &eby &d" + shooter.getName() + " &efrom &d" + String.format("%.1f", Double.valueOf(distance)) + " &eblocks away!"));

		for(Player player : Bukkit.getOnlinePlayers()) {
			VituzNametag.reloadPlayer(player);
			VituzNametag.reloadOthersFor(player);
		}
	}

	@EventHandler
	public void onSpeed(PlayerInteractEvent event) {		
		Player player = event.getPlayer();

		if(((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK))) {
		    if(!event.hasItem()) return;
		    
		    if(event.getItem().getType() == Material.SUGAR) {
				if(ArmorClassHandler.getEquippedClass(event.getPlayer()) != this) return;
				
				long remaining = speed_cooldowns.get(player.getUniqueId()) == speed_cooldowns.getNoEntryValue() ? -1L : speed_cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
				
				if(remaining > 0L) {
					player.sendMessage(Color.translate("&cYou can't use this for another &l" + DurationFormatUtils.formatDurationWords(remaining, true, true) + "&c!"));
				} else {
					ItemStack stack = player.getItemInHand();
					
					if(stack.getAmount() == 1) {
						player.setItemInHand(new ItemStack(Material.AIR, 1));
					} else {
						stack.setAmount(stack.getAmount() - 1);
					}

					EffectRestorerHandler.setRestoreEffect(player, speed_effect);
					
				
					speed_cooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + speed_cooldown_delay);
				}
		    }
		}
	}
		
	@EventHandler
	public void onJump(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if(((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK))) {
			if(!event.hasItem()) return;

			if(event.getItem().getType() == Material.FEATHER) {
				if(ArmorClassHandler.getEquippedClass(event.getPlayer()) != this) return;

				long remaining = jump_cooldowns.get(player.getUniqueId()) == jump_cooldowns.getNoEntryValue() ? -1L : jump_cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();

				if(remaining > 0L) {
					player.sendMessage(Color.translate("&cYou can't use this for another &l" + DurationFormatUtils.formatDurationWords(remaining, true, true) + "&c!"));
				} else {
					ItemStack stack = player.getItemInHand();
					
					if(stack.getAmount() == 1) {
						player.setItemInHand(new ItemStack(Material.AIR, 1));
					} else {
						stack.setAmount(stack.getAmount() - 1);
					}
					
					EffectRestorerHandler.setRestoreEffect(player, jump_effect);

					jump_cooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + jump_cooldown_delay);
				}
			}
		}
	}

	
	@Override
	public boolean isApplicableFor(Player player) {
		ItemStack helmet = player.getInventory().getHelmet();
		if((helmet == null) || (helmet.getType() != Material.LEATHER_HELMET)) return false;
		
		ItemStack chestplate = player.getInventory().getChestplate();
		if((chestplate == null) || (chestplate.getType() != Material.LEATHER_CHESTPLATE)) return false;
		
		ItemStack leggings = player.getInventory().getLeggings();
		if((leggings == null) || (leggings.getType() != Material.LEATHER_LEGGINGS)) return false;
		
		ItemStack boots = player.getInventory().getBoots();
		return (boots != null) && (boots.getType() == Material.LEATHER_BOOTS);
	}

	// Backup
		/*LeatherArmorMeta helmMeta = (LeatherArmorMeta) shooter.getInventory().getHelmet().getItemMeta();
		LeatherArmorMeta chestMeta = (LeatherArmorMeta) shooter.getInventory().getChestplate().getItemMeta();
		LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) shooter.getInventory().getLeggings().getItemMeta();
		LeatherArmorMeta bootsMeta = (LeatherArmorMeta) shooter.getInventory().getBoots().getItemMeta();

		org.bukkit.Color green = org.bukkit.Color.fromRGB(6717235);

		double r = random.nextDouble();

		r = random.nextDouble();

		if((r <= 0.5D) && (helmMeta.getColor().equals(green)) && (chestMeta.getColor().equals(green)) && (leggingsMeta.getColor().equals(green)) && (bootsMeta.getColor().equals(green))) {
			damaged.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 0));

			shooter.sendMessage(Color.translate("&eSince your &dArmor &eis green, you gave &d" + damaged.getName() + " &ethe poison effect for 6 seconds..."));
			damaged.sendMessage(Color.translate("&eSince &d" + shooter.getName() + "'s &earmor is green, you were given the poison effect for 6 seconds..."));
		}

		org.bukkit.Color blue = org.bukkit.Color.fromRGB(3361970);
		if((r <= 0.5D) && (helmMeta.getColor().equals(blue)) && (chestMeta.getColor().equals(blue)) && (leggingsMeta.getColor().equals(blue)) && (bootsMeta.getColor().equals(blue))) {
			damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 0));

			shooter.sendMessage(Color.translate("&eSince your &dArmor &eis blue, you gave &d" + damaged.getName() + " &ethe slowness effect for 6 seconds..."));
			damaged.sendMessage(Color.translate("&eSince &d" + shooter.getName() + "'s &earmor is blue, you were given the slowness effect for 6 seconds..."));
		}

		org.bukkit.Color gray = org.bukkit.Color.fromRGB(5000268);
		if((r <= 0.5D) && (helmMeta.getColor().equals(gray)) && (chestMeta.getColor().equals(gray)) && (leggingsMeta.getColor().equals(gray)) && (bootsMeta.getColor().equals(gray))) {
			damaged.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 0));

			shooter.sendMessage(Color.translate("&eSince your &dArmor &eis gray, you gave &d" + damaged.getName() + " &ethe blindness effect for 6 seconds..."));
			damaged.sendMessage(Color.translate("&eSince &d" + shooter.getName() + "'s &earmor is gray, you were given the blindness effect for 6 seconds..."));
		}

		org.bukkit.Color black = org.bukkit.Color.fromRGB(1644825);
		if((r <= 0.2D) && (helmMeta.getColor().equals(black)) && (chestMeta.getColor().equals(black)) && (leggingsMeta.getColor().equals(black)) && (bootsMeta.getColor().equals(black))) {
			damaged.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 120, 0));

			shooter.sendMessage(Color.translate("&eSince your &dArmor &eis black, you gave &d" + damaged.getName() + " &ethe wither effect for 6 seconds..."));
			damaged.sendMessage(Color.translate("&eSince &d" + shooter.getName() + "'s &earmor is black, you were given the wither effect for 6 seconds..."));
		}*/
}