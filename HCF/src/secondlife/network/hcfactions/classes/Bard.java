package secondlife.network.hcfactions.classes;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.classes.utils.ArmorClass;
import secondlife.network.hcfactions.classes.utils.ArmorClassHandler;
import secondlife.network.hcfactions.classes.utils.bard.BardData;
import secondlife.network.hcfactions.classes.utils.bard.EffectData;
import secondlife.network.hcfactions.classes.utils.bard.EffectRestorerHandler;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.timers.SpawnTagHandler;
import secondlife.network.vituz.utilties.Color;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Bard extends ArmorClass implements Listener {

	public static long buff_cooldown = TimeUnit.SECONDS.toMillis(10L);
	public static int team_radius = 25;
	public static long held_reapply_ticks = 20L;
	public static Map<UUID, BardData> bardDataMap = new HashMap<>();
	public static Map<Material, EffectData> bardEffects = new EnumMap<>(Material.class);
	public static TObjectLongMap<UUID> cooldowns = new TObjectLongHashMap<>();

	public Bard() {
		super("Bard", !HCFConfiguration.kitMap ? 3 : 1);

		passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
		passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
		passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
				
        bardEffects.put(Material.FERMENTED_SPIDER_EYE, new EffectData(60, new PotionEffect(PotionEffectType.INVISIBILITY, 120, 1),new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0)));
		bardEffects.put(Material.WHEAT, new EffectData(35, new PotionEffect(PotionEffectType.SATURATION, 120, 1), new PotionEffect(PotionEffectType.SATURATION, 100, 0)));
		bardEffects.put(Material.SUGAR, new EffectData(25, new PotionEffect(PotionEffectType.SPEED, 120, 2), new PotionEffect(PotionEffectType.SPEED, 100, 1)));
		bardEffects.put(Material.BLAZE_POWDER, new EffectData(50, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0)));
		bardEffects.put(Material.IRON_INGOT, new EffectData(35, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 2), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0)));
		bardEffects.put(Material.GHAST_TEAR, new EffectData(45, new PotionEffect(PotionEffectType.REGENERATION, 60, 2), new PotionEffect(PotionEffectType.REGENERATION, 100, 0)));
		bardEffects.put(Material.FEATHER, new EffectData(30, new PotionEffect(PotionEffectType.JUMP, 120, 5), new PotionEffect(PotionEffectType.JUMP, 100, 0)));
		bardEffects.put(Material.SPIDER_EYE, new EffectData(50, new PotionEffect(PotionEffectType.WITHER, 100, 1), null));
		bardEffects.put(Material.MAGMA_CREAM, new EffectData(10, new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 900, 0), new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 120, 0)));
	}

	@Override
	public boolean onEquip(Player player) {
		if(!super.onEquip(player)) return false;

		BardData bardData = new BardData();
		
		bardDataMap.put(player.getUniqueId(), bardData);
		
		bardData.startEnergyTracking();
		bardData.heldTask = new BukkitRunnable() {
			int lastEnergy;

            public void run() {
                ItemStack held = player.getItemInHand();
               
                if(held != null) {
                    EffectData bardEffect = bardEffects.get(held.getType());
                    
                    if(bardEffect == null) return;

                    if(!RegisterHandler.getInstancee().getFactionManager().getFactionAt(player.getLocation()).isSafezone()) {
                        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);
                        
                        if(playerFaction != null) {
                            Collection<Entity> nearbyEntities = player.getNearbyEntities(team_radius, team_radius, team_radius);
                           
                            for(Entity nearby : nearbyEntities) {
                                if(nearby instanceof Player && !player.equals(nearby)) {
                                    Player target = (Player) nearby;
                                    
                                    if(playerFaction.getMembers().containsKey(target.getName())) {
                                        EffectRestorerHandler.setRestoreEffect(target, bardEffect.heldable);
                                    }
                                }
                            }
                        }
                    }
                }
				
				int energy = (int) getEnergy(player);
				
				if(energy != 0 && energy != lastEnergy && (energy % 10 == 0 || lastEnergy - energy - 1 > 0 || energy == bardData.max_energy)) {
					lastEnergy = energy;
					
					player.sendMessage(Color.translate("&eBard Energy: &d" + energy));
				}
			}
		}.runTaskTimerAsynchronously(HCF.getInstance(), 0L, held_reapply_ticks);
		
		return true;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!event.hasItem()) return;
		
		if(event.getAction() == Action.RIGHT_CLICK_AIR || (!event.isCancelled() && event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			Player player = event.getPlayer();
			
			ItemStack stack = event.getItem();
			EffectData bardEffect = Bard.bardEffects.get(stack.getType());
			
			if(bardEffect == null || bardEffect.clickable == null) return;
			
			event.setUseItemInHand(Event.Result.DENY);
			BardData bardData = Bard.bardDataMap.get(player.getUniqueId());
			
			if(bardData != null) {
				if(!this.canUseBardEffect(player, bardData, bardEffect, true)) {
					return;
				}
				
				if(stack.getAmount() > 1) {
					stack.setAmount(stack.getAmount() - 1);

					SpawnTagHandler.applyBard(player);
				} else {
					player.setItemInHand(new ItemStack(Material.AIR, 1));
				}
				if(bardEffect != null && !RegisterHandler.getInstancee().getFactionManager().getFactionAt(player.getLocation()).isSafezone()) {
					PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);
					
					if(playerFaction != null && !bardEffect.clickable.getType().equals(PotionEffectType.WITHER)) {
						Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0, 25.0, 25.0);
						
						for(Entity nearby : nearbyEntities) {
							if(nearby instanceof Player && !player.equals(nearby)) {
								Player target = (Player) nearby;
								
								if(!playerFaction.getMembers().containsKey(target.getName())) continue;
								
								EffectRestorerHandler.setRestoreEffect(target, bardEffect.clickable);
							}
						}
					} else if (playerFaction != null && bardEffect.clickable.getType().equals(PotionEffectType.WITHER)) {
						Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0, 25.0, 25.0);
						
						for(Entity nearby : nearbyEntities) {
							if(nearby instanceof Player && !player.equals(nearby)) {
								Player target = (Player) nearby;
								
								if(playerFaction.getMembers().containsKey(target.getName())) continue;

								SpawnTagHandler.applyBard(target);

								EffectRestorerHandler.setRestoreEffect(target, bardEffect.clickable);
							}
						}
					} else if (bardEffect.clickable.getType().equals(PotionEffectType.WITHER)) {
						Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0, 25.0, 25.0);
						
						for(Entity nearby : nearbyEntities) {
							if(nearby instanceof Player && !player.equals(nearby)) {
								Player target = (Player) nearby;
								
								EffectRestorerHandler.setRestoreEffect(target, bardEffect.clickable);
							}
						}
					}
				}
				
				EffectRestorerHandler.setRestoreEffect(player, bardEffect.clickable);
				bardData.setBuffCooldown(buff_cooldown);
				
				this.setEnergy(player, this.getEnergy(player) - bardEffect.energyCost);
				
				player.sendMessage(Color.translate("&cYou have just used a &lBard Buff &cthat cost you &l" + bardEffect.energyCost + " &cof your Energy."));				
			}
		}
	}

	@Override
	public void onUnequip(Player player) {
		super.onUnequip(player);
		
		clearBardData(player.getUniqueId());
	}

	private void clearBardData(UUID uuid) {
		BardData bardData = bardDataMap.remove(uuid);
		
		if(bardData == null) return;
		if(bardData.heldTask == null) return;
		
		bardData.heldTask.cancel();
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		clearBardData(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onItemHeld(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		ArmorClass equipped = ArmorClassHandler.getEquippedClass(player);
		
		if(equipped == null) return;
		if(!equipped.equals(this)) return;
		
		long lastMessage = cooldowns.get(player.getUniqueId());
		long millis = System.currentTimeMillis();
		
		if(lastMessage != cooldowns.getNoEntryValue() && lastMessage - millis > 0L) return;
	}

	private boolean canUseBardEffect(Player player, BardData bardData, EffectData bardEffect, boolean sendFeedback) {
		String errorFeedback = null;
		double currentEnergy = bardData.getEnergy();
		
		if(bardEffect.energyCost > currentEnergy)  {
			errorFeedback = Color.translate("&cYou do not have enough energy for this! You need &l" + bardEffect.energyCost + " &cenergy, but you only have &l" + currentEnergy + "&c!");
		}

		long remaining = bardData.getRemainingBuffDelay() / 1000;
		
		if(remaining > 0L) {
			errorFeedback = Color.translate("&cYou can't use this for another &l" + remaining + " &cseconds.");
		}

		Faction factionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(player.getLocation());
		if(factionAt.isSafezone()) {
			errorFeedback = Color.translate("&cYou can't use &lBard effects&c while you are in spawn.");
		}

		if(sendFeedback && errorFeedback != null) player.sendMessage(errorFeedback);
	
		return errorFeedback == null;
	}

	@Override
	public boolean isApplicableFor(Player player) {
		ItemStack helmet = player.getInventory().getHelmet();
		if(helmet == null || helmet.getType() != Material.GOLD_HELMET) return false;

		ItemStack chestplate = player.getInventory().getChestplate();
		if(chestplate == null || chestplate.getType() != Material.GOLD_CHESTPLATE) return false;

		ItemStack leggings = player.getInventory().getLeggings();
		if(leggings == null || leggings.getType() != Material.GOLD_LEGGINGS) return false;

		ItemStack boots = player.getInventory().getBoots();
		return !(boots == null || boots.getType() != Material.GOLD_BOOTS);
	}

	public long getRemainingBuffDelay(Player player) {
		synchronized(bardDataMap) {
			BardData bardData = bardDataMap.get(player.getUniqueId());
			
			return bardData == null ? 0L : bardData.getRemainingBuffDelay();
		}
	}

	public double getEnergy(Player player) {
		synchronized(bardDataMap) {
			BardData bardData = bardDataMap.get(player.getUniqueId());
			
			return bardData == null ? 0 : bardData.getEnergy();
		}
	}

	public long getEnergyMillis(Player player) {
		synchronized(bardDataMap) {
			BardData bardData = bardDataMap.get(player.getUniqueId());
			
			return bardData == null ? 0 : bardData.getEnergyMillis();
		}
	}

	public double setEnergy(Player player, double energy) {
		BardData bardData = bardDataMap.get(player.getUniqueId());
		
		if(bardData == null) return 0.0;

		bardData.setEnergy(energy);
		return bardData.getEnergy();
	}
}
