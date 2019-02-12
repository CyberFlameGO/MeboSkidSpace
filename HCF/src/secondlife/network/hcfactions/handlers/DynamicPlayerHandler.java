package secondlife.network.hcfactions.handlers;

import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArrow;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.material.EnderChest;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.commands.arguments.SpawnCommand;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.events.KitMapEvent;
import secondlife.network.hcfactions.events.sumo.SumoEvent;
import secondlife.network.hcfactions.events.sumo.SumoPlayer;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.hcfactions.utilties.JavaUtils;
import secondlife.network.hcfactions.utilties.file.UtilitiesFile;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.*;
import secondlife.network.vituz.utilties.inventory.InventoryUtils;
import secondlife.network.vituz.visualise.VisualiseHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class DynamicPlayerHandler extends Handler implements Listener {

	public static List<Material> allowed;

	private HCF plugin = HCF.getInstance();

	public DynamicPlayerHandler(HCF plugin) {
		super(plugin);
		
		removeRecipe();
		
		allowed = Arrays.asList(Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS, Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS);

		Tasks.runTimer(() -> Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(entity -> {
            if(entity instanceof Item) {
                entity.remove();
            }
        })), 300L, 300L);

		Tasks.runTimer(() -> {
			Date now = new Date();

			if(now.getHours() < 10 || now.getHours() > 22 || now.getMinutes() != 1) return;

			int random = randomWithRange(1, 3);

			switch(random) {
				case 1:
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "koth start End");
					break;
				case 2:
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "koth start Sky");
					break;
				case 3:
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "koth start Ladder");
					break;
			}
		}, 600L, 600L);

		Bukkit.getPluginManager().registerEvents(this, this.getInstance());
	}

	int randomWithRange(int min, int max) {
		int range = (max - min) + 1;
		return (int)(Math.random() * range) + min;
	}

	@EventHandler
	public void onEnderChest(PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(event.getClickedBlock().getType() != Material.ENDER_CHEST) return;
			
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if(player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
				for(PotionEffect effect : player.getActivePotionEffects()) {
					if(effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
						int level = effect.getAmplifier() + 1;

						if (level == 1) {
							event.setDamage(10.0D * event.getDamage() / (10.0D + 13.0D * level) + 13.0D * event.getDamage() * level * 10 / 100.0D / (10.0D + 13.0D * level));
						} else {
							event.setDamage(10.0D * event.getDamage() / (10.0D + 13.0D * level) + 13.0D * event.getDamage() * level * 20 / 100.0D / (10.0D + 13.0D * level));
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if(!(event.getInventory() instanceof EnderChest)) return;
		
		event.setCancelled(true);
	}
    
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();	
		LivingEntity killer = entity.getKiller();
		
		if(!(killer instanceof Player)) return;
		
		Player player = (Player) killer;

		if(player.getItemInHand() == null) return;
		if(!player.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) return;

		int amplifier = player.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) + 45;
		
		event.setDroppedExp(event.getDroppedExp() * amplifier);
	}
    
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if(!(event.getEntity() instanceof Arrow)) return;
        
		Arrow arrow = (Arrow) event.getEntity();

		if(!(arrow.getShooter() instanceof Player) || ((CraftArrow) arrow).getHandle().fromPlayer == 2) {
			arrow.remove();
		}
	}
	
	@EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {        
        if(!(event.getVehicle() instanceof Boat)) return;
        
		Boat boat = (Boat) event.getVehicle();
		Block belowBlock = boat.getLocation().add(0.0, -1.0, 0.0).getBlock();

		if(belowBlock.getType() == Material.WATER) return;
		if(belowBlock.getType() == Material.STATIONARY_WATER) return;
		
		boat.remove();
    }
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack item = event.getItem();
		Block block = event.getClickedBlock();
		
		if(item == null) return;
		
		if(!item.getType().equals(Material.ENCHANTED_BOOK)) return;
		if(action.equals(Action.LEFT_CLICK_BLOCK)) {
			if(!block.getType().equals(Material.ENCHANTMENT_TABLE)) return;
			if(player.getGameMode().equals(GameMode.CREATIVE)) return;
			
			if(!(item.getItemMeta() instanceof EnchantmentStorageMeta)) return;

			EnchantmentStorageMeta enchantmentMeta = (EnchantmentStorageMeta) item.getItemMeta();
			
			for(Enchantment enchantment : enchantmentMeta.getStoredEnchants().keySet()) {
				enchantmentMeta.removeStoredEnchant(enchantment);
			}
			
			event.setCancelled(true);
			
			player.setItemInHand(new ItemStack(Material.BOOK, 1));
			player.sendMessage(Color.translate("&eYou have removed all &dEnchantments &efrom this book!"));
		}
	}

	public static void handleFirstMove(Player player, Location from, Location to) {
		if(!SpawnCommand.teleporting.containsKey(player)) return;

		if(from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) return;

		int runnable = SpawnCommand.teleporting.get(player);

		Bukkit.getScheduler().cancelTask(runnable);
		SpawnCommand.teleporting.remove(player);

		player.sendMessage(Color.translate("&e&lSPAWN TELEPORT &c&lCANCELLED!"));
	}

	public static void handleSecondMove(Player player) {
		Location loc = player.getLocation();

		if((loc.getWorld().getEnvironment() == World.Environment.THE_END) && ((loc.getBlock().getType() == Material.WATER) || (loc.getBlock().getType() == Material.STATIONARY_WATER))) {
			String strin = UtilitiesFile.configuration.getString("World-Spawn.end-exit");

			if(strin == null) {
				player.sendMessage(Color.translate("&c&lEnd Exit is not set please contact a Staff Member!"));
				return;
			}

			Location spawn = StringUtils.destringifyLocation(strin);

			player.teleport(spawn);
		}

		if((loc.getWorld().getEnvironment() == World.Environment.NETHER) && ((loc.getBlock().getType() == Material.WATER) || (loc.getBlock().getType() == Material.STATIONARY_WATER))) {
			String strin = UtilitiesFile.configuration.getString("World-Spawn.world-spawn");

			if(strin == null) {
				player.sendMessage(Color.translate("&c&lSpawn is not set please contact a Staff Member!"));
				return;
			}

			Location spawn = StringUtils.destringifyLocation(strin);

			player.teleport(spawn);
		}
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		
		Player player = (Player) event.getEntity();
			
		if(!SpawnCommand.teleporting.containsKey(player)) return;
		
		int runnable = SpawnCommand.teleporting.get(player);
		
		Bukkit.getScheduler().cancelTask(runnable);
		SpawnCommand.teleporting.remove(player);
		
		player.sendMessage(Color.translate("&e&lSPAWN TELEPORT &c&lCANCELLED!"));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();    
        Player killer = event.getEntity().getKiller();
		
        if(HCFConfiguration.kitMap) {
            new BukkitRunnable() {
                public void run() {
                    if(player.isOnline()) {
                        player.spigot().respawn();
                    }
                }
            }.runTaskLater(this.getInstance(), 1L);
        }
        
		new BukkitRunnable() {
			public void run() {
				if(!HCFConfiguration.kitMap) {
					ServerUtils.sendToServer(player, "Hub");
				}
			}
		}.runTaskLater(this.getInstance(), 2L);

		HCFData data = HCFData.getByName(player.getName());
        
		data.setDeaths(data.getDeaths() + 1);

		PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

		if(data.isLightning()) {
			player.getLocation().getWorld().strikeLightningEffect(player.getLocation());
		}

		if (playerFaction != null) {
			Faction factionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(player.getLocation());
			double dtrLoss = (1.0D * factionAt.getDtrLossMultiplier());
			double newDtr = playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - dtrLoss);

			Role role = playerFaction.getMember(player.getName()).getRole();

			if(HCFConfiguration.kitMap) {
				playerFaction.setRemainingRegenerationTime(TimeUnit.SECONDS.toMillis(1L));
			} else {
				playerFaction.setRemainingRegenerationTime(TimeUnit.MINUTES.toMillis(40L) + (playerFaction.getOnlinePlayers().size() * TimeUnit.MINUTES.toMillis(2L)));
			}

			playerFaction.setPoints(playerFaction.getPoints() - 5);
			playerFaction.broadcast(Color.translate("&cMember Death: &d" + role.getAstrix() + player.getName()));
			playerFaction.broadcast(Color.translate("&cDTR: &d" + JavaUtils.format(newDtr, 2) + '/' + JavaUtils.format(playerFaction.getMaximumDeathsUntilRaidable(), 2)));

			playerFaction.broadcast("&eYour faction has lost &d5 points&e because &d" + player.getName() + " &edied!");
			Msg.logConsole("&c" + player.getName() + " &4[" + playerFaction.getName() + "] &cwas killed! DTR -> &4" + JavaUtils.format(newDtr, 2));
		}

		if(killer == null) return;

		PlayerFaction killerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(killer);

		if(killerFaction != null) {
			killerFaction.setPoints(killerFaction.getPoints() + 5);
			killerFaction.broadcast("&eYour faction has gotten &d5 points&e because &d" + killer.getName() + " &e killed &d" + player.getName());
		}

		HCFData killerData = HCFData.getByName(killer.getName());

		killerData.setKills(killerData.getKills() + 1);

		if(HCFConfiguration.kitMap) {
			killerData.setBalance(killerData.getBalance() + 100);

			killer.sendMessage(
					Color.translate("&6You earned &d$100 &6for killing &d" + player.getDisplayName() + "&6."));

			if(data.getBalance() >= 50) {
				data.setBalance(data.getBalance() - 50);
				player.sendMessage(Color.translate("&6You lost &d$50 &6because &d" + killer.getName() + " &6killed you."));
			}
		} else {
			player.sendMessage(Color.translate("&6You lost &d$" + data.getBalance() + " &6because &d" + killer.getDisplayName() + " &6killed you."));

			killerData.setBalance(killerData.getBalance() + data.getBalance());
			killer.sendMessage(Color.translate("&6You earned &d$" + data.getBalance() + " &6for killing &d" + player.getDisplayName() + "&6."));

			data.setBalance(0);
		}
    }
	
	@EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if(!(event.getEntity() instanceof Creeper)) return;
        
		event.setCancelled(true);
    }
    
    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
    	if(!(event.getEntity() instanceof Creeper)) return;
    	
		event.setCancelled(true);
    }
    
    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
    	ItemStack stack = event.getItem();
        
    	if(stack == null) return;
    	if(!allowed.contains(stack.getType())) return;
    	if(ThreadLocalRandom.current().nextInt(3) == 0) return;
    	
		event.setCancelled(true);
    }
	
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        if(player.getWorld().getEnvironment() == World.Environment.NETHER) return;
        if(!(event.getBlock().getState() instanceof CreatureSpawner)) return;
        if(player.hasPermission(Permission.ADMIN_PERMISSION)) return;

		event.setCancelled(true);
		
		player.sendMessage(Color.translate("&cYou can't break spawners in the nether."));
    }

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			HCFData playerData = HCFData.getByName(player.getName());

			if(playerData.isEvent()) {
				KitMapEvent event = this.plugin.getEventManager().getEventPlaying(player);

				if(event != null) {

					if(event instanceof SumoEvent) {
						SumoEvent sumoEvent = (SumoEvent) event;
						SumoPlayer sumoPlayer = sumoEvent.getPlayer(player);

						if (sumoPlayer != null && sumoPlayer.getState() == SumoPlayer.SumoState.FIGHTING) {
							e.setCancelled(false);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {

		if(!(e.getEntity() instanceof Player)) {
			e.setCancelled(true);
			return;
		}

		Player entity = (Player) e.getEntity();

		Player damager;

		if (e.getDamager() instanceof Player) {
			damager = (Player) e.getDamager();
		} else if (e.getDamager() instanceof Projectile) {
			damager = (Player) ((Projectile) e.getDamager()).getShooter();
		} else {
			return;
		}

		HCFData entityData = HCFData.getByName(entity.getName());
		HCFData damagerData = HCFData.getByName(damager.getName());

		boolean isEventEntity = this.plugin.getEventManager().getEventPlaying(entity) != null;
		boolean isEventDamager = this.plugin.getEventManager().getEventPlaying(damager) != null;

		KitMapEvent eventDamager = this.plugin.getEventManager().getEventPlaying(damager);
		KitMapEvent eventEntity = this.plugin.getEventManager().getEventPlaying(entity);

		if (isEventDamager && eventDamager instanceof SumoEvent && ((SumoEvent) eventDamager).getPlayer(damager).getState() != SumoPlayer.SumoState.FIGHTING || isEventEntity &&  eventDamager instanceof SumoEvent && ((SumoEvent) eventEntity).getPlayer(entity).getState() != SumoPlayer.SumoState.FIGHTING) {
			e.setCancelled(true);
			return;
		}

		if(entityData.isEvent() && eventEntity instanceof SumoEvent || damagerData.isEvent() && eventDamager instanceof SumoEvent) {
			e.setDamage(0.0D);
			return;
		}
	}

	@EventHandler
	public void oNint(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		HCFData playerData = HCFData.getByName(player.getName());

		if (playerData.isSpectating()) {
			event.setCancelled(true);
		}

		if (event.getAction().name().startsWith("RIGHT_")) {
			ItemStack item = event.getItem();

			if (playerData.isEvent()) {
				if (item == null) {
					return;
				}

				KitMapEvent practiceEvent = this.plugin.getEventManager().getEventPlaying(player);

				if (item.getType() == Material.NETHER_STAR) {
					if (practiceEvent != null) {
						practiceEvent.leave(player);
					}
				}
			}
		}
	}

	@EventHandler
	public void onFix1(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (!player.getGameMode().equals(GameMode.CREATIVE)) {
			HCFData playerData = HCFData.getByName(player.getName());

			if(playerData.isEvent()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onFix1(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		if (!player.getGameMode().equals(GameMode.CREATIVE)) {
			HCFData playerData = HCFData.getByName(player.getName());

			if(playerData.isEvent()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (!player.getGameMode().equals(GameMode.CREATIVE)) {
			HCFData playerData = HCFData.getByName(player.getName());

			if(playerData.isEvent()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		event.setJoinMessage("");

		if(!player.hasPlayedBefore()) {
			new BukkitRunnable() {
				public void run() {
					String strin = UtilitiesFile.configuration.getString("World-Spawn.world-spawn");

					if(strin == null) {
						player.sendMessage(Color.translate("&c&lSpawn is not set please contact a Staff Member!"));
						return;
					}

					Location spawn = StringUtils.destringifyLocation(strin);

					player.teleport(spawn);
				}
			}.runTaskLater(this.getInstance(), 5L);
						
			try {
				String items = UtilitiesFile.getString("first-join-items");

				player.getInventory().setContents(InventoryUtils.fromBase64(items).getContents());

				player.updateInventory();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		if(!player.isOp() && player.getGameMode().equals(GameMode.CREATIVE)) {
			player.setGameMode(GameMode.SURVIVAL);
		}
		
		if(player.hasPotionEffect(PotionEffectType.SATURATION)) {
			player.removePotionEffect(PotionEffectType.SATURATION);
		}

		Tasks.runLater(() -> {
			VituzNametag.reloadPlayer(player);
			VituzNametag.reloadOthersFor(player);
		}, 20L);
	}
	
	@EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {		
        if(!(event.getEntity() instanceof Player)) return;
        
		Player player = (Player) event.getEntity();
		player.setSaturation(1000.0f);
		player.setSaturation(10.0f);

		if(HCFConfiguration.kitMap) return;
		if(!(event.getEntity() instanceof Player)) return;
		
		event.setCancelled(true);

		event.setFoodLevel(20);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		HCFData data = HCFData.getByName(event.getPlayer().getName());

		KitMapEvent practiceEvent = this.plugin.getEventManager().getEventPlaying(event.getPlayer());
		if (practiceEvent != null) {
			practiceEvent.leave(event.getPlayer());
		}

		VisualiseHandler.clearVisualBlocks(event.getPlayer(), null, null, false);
		
		data.setClaimMap(false);
	}
	
	@EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		HCFData data = HCFData.getByName(event.getPlayer().getName());
		
		VisualiseHandler.clearVisualBlocks(event.getPlayer(), null, null, false);
		data.setClaimMap(false);
	}
    
    @EventHandler
	public void onPortalEnters(PlayerPortalEvent event) {
		if(event.getCause() != TeleportCause.END_PORTAL) return;
		if(event.getTo().getWorld().getEnvironment() != Environment.THE_END) return;
		
		event.setCancelled(true);

		String strin = UtilitiesFile.configuration.getString("World-Spawn.end-spawn");

		if(strin == null) {
			event.getPlayer().sendMessage(Color.translate("&c&lEnd Spawn is not set please contact a Staff Member!"));
			return;
		}

		Location spawn = StringUtils.destringifyLocation(strin);

		event.getPlayer().teleport(spawn);
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		String strin = UtilitiesFile.configuration.getString("World-Spawn.world-spawn");

		if(strin == null) {
			event.getPlayer().sendMessage(Color.translate("&c&lSpawn is not set please contact a Staff Member!"));
			return;
		}

		Location spawn = StringUtils.destringifyLocation(strin);

		event.setRespawnLocation(spawn);
	}

    @EventHandler
    public void onnPlayerBedEnter(PlayerBedEnterEvent event) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(Color.translate("&cBeds are not enabled."));
    }
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		event.setCancelled(true);	
	}
	
	
	@EventHandler
	public void onEntityExplodeGlavni(EntityExplodeEvent event) {
		if(event.isCancelled()) return;
		
		event.blockList().clear();
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		if(event.getCause() != IgniteCause.SPREAD) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event) {
		event.setCancelled(true);	
	}

	private static void removeRecipe() {
		for(Iterator<Recipe> iterator = Bukkit.recipeIterator(); iterator.hasNext();) {
			if(iterator.next().getResult().getType() != Material.ENDER_CHEST) return;

			iterator.remove();
		}
	}
}
