package secondlife.network.hcfactions.classes;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
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
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.classes.utils.ArmorClass;
import secondlife.network.hcfactions.classes.utils.ArmorClassHandler;
import secondlife.network.hcfactions.classes.utils.bard.EffectRestorerHandler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Tasks;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Rogue extends ArmorClass implements Listener {

	public static ArrayList<UUID> cooldown = new ArrayList<>();

	public static TObjectLongMap<UUID> speed_cooldowns = new TObjectLongHashMap<>();
	public static TObjectLongMap<UUID> jump_cooldowns = new TObjectLongHashMap<UUID>();
	
	public static PotionEffect speed_effect = new PotionEffect(PotionEffectType.SPEED, 160, 3);
	public static PotionEffect jump_effect = new PotionEffect(PotionEffectType.JUMP, 160, 4);
	
	public static long speed_cooldown_delay = TimeUnit.SECONDS.toMillis(45L);
	public static long jump_cooldown_delay = TimeUnit.MINUTES.toMillis(1L);
	
	public Rogue() {
		super("Rogue", !HCFConfiguration.kitMap ? 3 : 1);

		passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
		passiveEffects.add(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
		passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.isCancelled()) return;

		if(!(event.getEntity() instanceof Player)) return;
		if(!(event.getDamager() instanceof Player)) return;
		
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		Player attacker = (Player) damager;
		
		if(ArmorClassHandler.getEquippedClass(attacker) != this) return;
		
		ItemStack stack = attacker.getItemInHand();
		
		if(stack == null) return;
		if(stack.getType() != Material.GOLD_SWORD) return;
		if(!stack.getEnchantments().isEmpty()) return;
			
		Player player = (Player) entity;
		
		if(direction(attacker) != direction(player)) return;

		if(cooldown.contains(damager.getUniqueId())) {
			((Player) damager).sendMessage(Color.translate("&cYou are on cooldown!"));
			return;
		}

		Damageable damage = player;
		
		if(damage.getHealth() <= 0.0D) return;
		
		if(damage.getHealth() <= 6.0D) {
			damage.damage(20.0D);
		} else {
			damage.setHealth(damage.getHealth() - 6.0D);
		}
		
		player.sendMessage(Color.translate("&d" + attacker.getName() + " &ehas backstabbed you!"));
		player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
		
		attacker.sendMessage(Color.translate("&eYou have backstabbed &d" + player.getName() + "&e!"));
		
		attacker.setItemInHand(new ItemStack(Material.AIR, 1));
		attacker.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
		
		attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
		
		event.setCancelled(true);

		cooldown.add(damager.getUniqueId());

		Tasks.runLater(() -> {
			if(cooldown.contains(damager.getUniqueId())) cooldown.remove(damager.getUniqueId());
		}, 60L);
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
		if (helmet == null || helmet.getType() != Material.CHAINMAIL_HELMET) return false;
		

		ItemStack chestplate = player.getInventory().getChestplate();
		if(chestplate == null || chestplate.getType() != Material.CHAINMAIL_CHESTPLATE) return false;
		
		ItemStack leggings = player.getInventory().getLeggings();
		if(leggings == null || leggings.getType() != Material.CHAINMAIL_LEGGINGS) return false;
		

		ItemStack boots = player.getInventory().getBoots();
		return !(boots == null || boots.getType() != Material.CHAINMAIL_BOOTS);
	}
	
	public Byte direction(Player player) {
		double rotation = (player.getLocation().getYaw() - 90) % 360;

		if(rotation < 0) {
			rotation += 360.0;
		}

		if(0 <= rotation && rotation < 22.5) {
			return 0xC; // S > E
		} else if(22.5 <= rotation && rotation < 67.5) {
			return 0xE; // SW > SE
		} else if(67.5 <= rotation && rotation < 112.5) {
			return 0x0; // W > E
		} else if(112.5 <= rotation && rotation < 157.5) {
			return 0x2; // NW > SW
		} else if(157.5 <= rotation && rotation < 202.5) {
			return 0x4; // N > W
		} else if(202.5 <= rotation && rotation < 247.5) {
			return 0x6; // NE > NW
		} else if(247.5 <= rotation && rotation < 292.5) {
			return 0x8; // E > N
		} else if(292.5 <= rotation && rotation < 337.5) {
			return 0xA; // SE > NE
		} else if(337.5 <= rotation && rotation < 360.0) {
			return 0xC; // S > E
		} else {
			return null;
		}
	}
}
