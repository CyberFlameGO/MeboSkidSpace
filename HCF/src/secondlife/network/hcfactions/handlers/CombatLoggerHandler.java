package secondlife.network.hcfactions.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.timers.HomeHandler;
import secondlife.network.hcfactions.timers.SpawnTagHandler;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.hcfactions.utilties.JavaUtils;
import secondlife.network.vituz.utilties.Color;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CombatLoggerHandler extends Handler implements Listener {

	public static HashMap<String, List<Player>> combatLoggers;
	
	public CombatLoggerHandler(HCF plugin) {
		super(plugin);
		
		combatLoggers = new HashMap<String, List<Player>>();
		
		new BukkitRunnable() {
			public void run() {
				removeVillagers();
				removeVillagers2();
			}
		}.runTaskLater(this.getInstance(), 200L);
		
		Bukkit.getPluginManager().registerEvents(this, this.getInstance());
	}
	
	public static void disable() {
		combatLoggers.clear();
	}
	
	public void removeVillagers() {
		for(World world : Bukkit.getWorlds()) {
			for(Villager villager : world.getEntitiesByClass(Villager.class)) {
				villager.remove();
			}
		}
	}
	
	public void removeVillagers2() {
		for(World world : Bukkit.getWorlds()) {
			for(Entity entity : world.getEntities()) {
				if(entity instanceof Villager) {
					entity.remove();
				}
			}
		}
	}
	
	public void onJoin(Player player) {
		for(Villager villager : player.getWorld().getEntitiesByClass(Villager.class)) {
			if(!villager.isDead() && villager.hasMetadata("CombatLogger")) {
				if(villager.getCustomName().equals(player.getName())) {
					villager.removeMetadata("CombatLogger", HCF.getInstance());
					villager.removeMetadata("Player", HCF.getInstance());
					villager.removeMetadata("Contents", HCF.getInstance());
					villager.removeMetadata("Armor", HCF.getInstance());
					villager.remove();
				}
			}
		}
	}
	
	public void onQuit(Player player) {
		if(((Damageable)player).getHealth() == 0.0) return;
		if(player.hasPermission("secondlife.staff")) return;
		if(player.hasMetadata("LogoutCommand")) return;

		if(HomeHandler.getNearbyEnemies(player, 64) <= 0) return;

		Location location = player.getLocation();

		if(RegisterHandler.getInstancee().getFactionManager().getFactionAt(location).isSafezone()) return;

		this.spawnVillager(player);
	}


	public void spawnVillager(Player player) {
		Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
		
		villager.setCustomName(player.getName());
		villager.setCustomNameVisible(true);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
		villager.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 100));
		villager.setMetadata("CombatLogger", new FixedMetadataValue(this.getInstance(), player.getUniqueId()));
		villager.setMetadata("Player", new FixedMetadataValue(this.getInstance(), player));
		villager.setMetadata("Contents", new FixedMetadataValue(this.getInstance(), player.getInventory().getContents()));
		villager.setMetadata("Armor", new FixedMetadataValue(this.getInstance(), player.getInventory().getArmorContents()));
		villager.setMaxHealth(40);
		villager.setHealth(((Damageable)villager).getMaxHealth());
		
		new BukkitRunnable() {
			public void run() {
				if(villager != null && !villager.isDead()) {
					villager.removeMetadata("CombatLogger", HCF.getInstance());
					villager.removeMetadata("Player", HCF.getInstance());
					villager.removeMetadata("Contents", HCF.getInstance());
					villager.removeMetadata("Armor", HCF.getInstance());
					villager.remove();
				}
			}
		}.runTaskLater(this.getInstance(), 20 * 20);
	}
	
	public HashMap<String, List<Player>> getCombatLoggers() {
		return combatLoggers;
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		
		if(!(entity instanceof Villager)) return;
		
		Villager villager = (Villager) entity;
		Entity killer = entity.getKiller();
		
		if(!villager.hasMetadata("CombatLogger")) return;
		
		Player player = (Player) villager.getMetadata("Player").get(0).value();
		
		HCFData data = HCFData.getByName(player.getName());
		data.setDeaths(data.getDeaths() + 1);

		if(killer instanceof Player) {
			HCFData kdata = HCFData.getByName(killer.getName());
			kdata.setKills(kdata.getKills() + 1);

			Bukkit.broadcastMessage(Color.translate("&c" + villager.getCustomName() + "&7[&f" + data.getKills() + "&7] &7(Combat Logger) &ewas slain by &c" + ((Player) killer).getName() + "&7[&f" + kdata.getKills() + "&7]"));
		} else {
			Bukkit.broadcastMessage(Color.translate("&c" + villager.getCustomName() + "&7[&f" + data.getKills() + "&7] &7(Combat Logger) &edied"));
		}

		ItemStack[] contentsArray = (ItemStack[]) villager.getMetadata("Contents").get(0).value();
		ItemStack[] armorArray = (ItemStack[]) villager.getMetadata("Armor").get(0).value();

		for(ItemStack content : contentsArray) {
			if(content != null && !content.getType().equals(Material.AIR)) {
				villager.getWorld().dropItemNaturally(villager.getLocation(), content);
			}
		}
		
		for(ItemStack armor : armorArray) {
			if(armor != null && !armor.getType().equals(Material.AIR)) {
				villager.getWorld().dropItemNaturally(villager.getLocation(), armor);
			}
		}

		data.setCombatLogger(true);

		PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

		if(playerFaction == null) return;
		
		Faction factionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(player.getLocation());
		double dtrLoss = (1.0D * factionAt.getDtrLossMultiplier());
		double newDtr = playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - dtrLoss);

		Role role = playerFaction.getMember(player.getName()).getRole();

		if(HCFConfiguration.kitMap) {
			playerFaction.setRemainingRegenerationTime(TimeUnit.SECONDS.toMillis(1L));
		} else {
			playerFaction.setRemainingRegenerationTime(TimeUnit.MINUTES.toMillis(40L) + (playerFaction.getOnlinePlayers().size() * TimeUnit.MINUTES.toMillis(2L)));
		}

		playerFaction.broadcast(Color.translate("&cMember Death: &d" + role.getAstrix() + player.getName()));
		playerFaction.broadcast(Color.translate("&cDTR: &d" + JavaUtils.format(newDtr, 2) + '/' + JavaUtils.format(playerFaction.getMaximumDeathsUntilRaidable(), 2)));
	}
    			
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Villager) {
			Villager villager = (Villager) event.getEntity();
			
			if(event.getDamager() instanceof Player) {
				Player player = (Player) event.getDamager();
				
				if(!villager.hasMetadata("CombatLogger")) return;
				if(!(event.getDamager() instanceof Player)) return;
				
				Player pvillager = (Player) villager.getMetadata("Player").get(0).value();

				PlayerFaction faction1 = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);
				PlayerFaction faction2 = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(pvillager);
				
				if(faction1 != null && faction2 != null && faction1 == faction2) {
					((Player) event.getDamager()).sendMessage(Color.translate("&eYou can't hurt &2" + pvillager.getName() + " &ebecause he is in your faction."));
					event.setCancelled(true);
				} else {
					SpawnTagHandler.applyOther((Player) event.getDamager());
				}

				if(!combatLoggers.containsKey(villager.getCustomName())) return;
				
				if(combatLoggers.get(villager.getCustomName()).contains(player)) {
					event.setCancelled(true);
				} else {
					new BukkitRunnable() {
						public void run() {
							villager.setVelocity(new Vector(0, 0, 0));
						}
					}.runTaskLater(this.getInstance(), 1L);
				}
			} else if(event.getDamager() instanceof Projectile) {
				Projectile projectile = (Projectile) event.getDamager();
				
				if(projectile.getShooter() instanceof Player) {
					Player shooter = (Player) projectile.getShooter();
					
					if(shooter != event.getEntity()) {
						if(!villager.hasMetadata("CombatLogger")) return;
												
						Player pvillager = (Player) villager.getMetadata("Player").get(0).value();

						PlayerFaction faction1 = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(shooter);
						PlayerFaction faction2 = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(pvillager);
						
						if(faction1 != null && faction2 != null && faction1 == faction2) {
							shooter.sendMessage(Color.translate("&eYou can't hurt &2" + pvillager.getName() + " &ebecause he is in your faction."));
							event.setCancelled(true);
						} else {
							SpawnTagHandler.applyOther(shooter);
						}

						if(!combatLoggers.containsKey(villager.getCustomName())) return;
						
						if(combatLoggers.get(villager.getCustomName()).contains(shooter)) {
							event.setCancelled(true);
						} else {
							new BukkitRunnable() {
								public void run() {
									villager.setVelocity(new Vector(0, 0, 0));
								}
							}.runTaskLater(this.getInstance(), 1L);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if(!event.getRightClicked().hasMetadata("CombatLogger")) return;
        
		event.setCancelled(true);
    }

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		for(Entity entity : event.getChunk().getEntities()) {
			if(!entity.hasMetadata("CombatLogger")) return;
			if (entity.isDead()) return;
			
			event.setCancelled(true);
		}
	}
    
    @EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		onJoin(player);

		if(player.hasMetadata("LogoutCommand")) {
			player.removeMetadata("LogoutCommand", HCF.getInstance());
		}

		new BukkitRunnable() {
			public void run() {
				HCFData data = HCFData.getByName(player.getName());

				if(data.isCombatLogger()) {
					player.spigot().respawn();

					player.teleport(new Location(Bukkit.getWorld("world"), 0, 100, 0));

					player.sendMessage(Color.translate("&eYou were killed because your &dCombat Logger &edied."));

					player.getInventory().clear();
					player.getInventory().setArmorContents(new ItemStack[4]);
				}

				data.setCombatLogger(false);
			}
		}.runTaskLater(this.getInstance(), 5L);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		this.onQuit(player);
	}
}
