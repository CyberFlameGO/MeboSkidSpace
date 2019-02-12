package secondlife.network.hcfactions.factions.handlers;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Cauldron;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.type.ClaimableFaction;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.type.games.CapturableFaction;
import secondlife.network.hcfactions.factions.type.system.WarzoneFaction;
import secondlife.network.hcfactions.factions.utils.CaptureZone;
import secondlife.network.hcfactions.factions.utils.events.FactionPlayerClaimEnterEvent;
import secondlife.network.hcfactions.factions.utils.events.capzone.CaptureZoneEnterEvent;
import secondlife.network.hcfactions.factions.utils.events.capzone.CaptureZoneLeaveEvent;
import secondlife.network.hcfactions.factions.utils.struction.Raidable;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.timers.EnderpearlHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.hcfactions.utilties.file.UtilitiesFile;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.StringUtils;
import secondlife.network.vituz.utilties.cuboid.Cuboid;

public class ProtectionHandler extends Handler implements Listener {
	
    private static ImmutableMultimap<Material, Material> ITEM_BLOCK_INTERACTABLES = ImmutableMultimap.<Material, Material> builder().put(Material.DIAMOND_HOE, Material.GRASS).put(Material.GOLD_HOE, Material.GRASS).put(Material.IRON_HOE, Material.GRASS).put(Material.STONE_HOE, Material.GRASS).put(Material.WOOD_HOE, Material.GRASS).build();
    private static ImmutableSet<Material> BLOCK_INTERACTABLES = Sets.immutableEnumSet(Material.BED, Material.BED_BLOCK, Material.BEACON, Material.FENCE_GATE, Material.IRON_DOOR, Material.TRAP_DOOR, Material.WOOD_DOOR, Material.WOODEN_DOOR, Material.IRON_DOOR_BLOCK, Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.BURNING_FURNACE, Material.BREWING_STAND, Material.HOPPER, Material.DROPPER, Material.DISPENSER, Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.ENCHANTMENT_TABLE, Material.WORKBENCH, Material.ANVIL, Material.LEVER, Material.FIRE);
	
	public ProtectionHandler(HCF plugin) {
		super(plugin);
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

    @EventHandler(ignoreCancelled = true, priority=EventPriority.HIGH)
    public void onCreatureLimit(CreatureSpawnEvent event) {
        if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) return;

        switch (event.getSpawnReason()) {
            case NATURAL: {
                if(event.getLocation().getChunk().getEntities().length <= 15) break;

                event.setCancelled(true);
                break;
            }
            case CHUNK_GEN: {
                if(event.getLocation().getChunk().getEntities().length <= 20) break;

                event.setCancelled(true);
                break;
            }
            case SPAWNER: {
                if(event.getLocation().getChunk().getEntities().length <= 30) break;

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(((event.getDamager() instanceof Enderman)) || ((event.getDamager() instanceof MagmaCube)) || ((event.getDamager() instanceof Slime))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        switch (event.getCause()) {
            case FLINT_AND_STEEL:
            case ENDER_CRYSTAL:
                return;
            default:
                break;
        }

        Faction factionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(event.getBlock().getLocation());

        if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onStickyPistonExtend(BlockPistonExtendEvent event) {
        Block block = event.getBlock();
        Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);
        
        if(targetBlock.isEmpty() || targetBlock.isLiquid()) { 
            Faction targetFaction = RegisterHandler.getInstancee().getFactionManager().getFactionAt(targetBlock.getLocation());
            
            if(targetFaction instanceof Raidable && !((Raidable) targetFaction).isRaidable() && targetFaction != RegisterHandler.getInstancee().getFactionManager().getFactionAt(block)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onStickyPistonRetract(BlockPistonRetractEvent event) {
        if(!event.isSticky()) return; 

        Location retractLocation = event.getRetractLocation();
        Block retractBlock = retractLocation.getBlock();
        
        if(!retractBlock.isEmpty() && !retractBlock.isLiquid()) {
            Block block = event.getBlock();
            Faction targetFaction = RegisterHandler.getInstancee().getFactionManager().getFactionAt(retractLocation);
            
            if(targetFaction instanceof Raidable && !((Raidable) targetFaction).isRaidable() && targetFaction != RegisterHandler.getInstancee().getFactionManager().getFactionAt(block)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockFromTo(BlockFromToEvent event) {
        Block toBlock = event.getToBlock();
        Block fromBlock = event.getBlock();
        Material fromType = fromBlock.getType();
        Material toType = toBlock.getType();

        if(!(toType != Material.REDSTONE_WIRE && toType != Material.TRIPWIRE || fromType != Material.AIR && fromType != Material.STATIONARY_LAVA && fromType != Material.LAVA)) toBlock.setType(Material.AIR);
        if(!(toBlock.getType() != Material.WATER && toBlock.getType() != Material.STATIONARY_WATER && toBlock.getType() != Material.LAVA && toBlock.getType() != Material.STATIONARY_LAVA || canBuildAt(fromBlock.getLocation(), toBlock.getLocation()))) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if(event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Faction toFactionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(event.getTo());
            
            if(toFactionAt.isSafezone() && !RegisterHandler.getInstancee().getFactionManager().getFactionAt(event.getFrom()).isSafezone()) {
            	Player player = event.getPlayer();
                
                player.sendMessage(Color.translate("&c&lInvalid Pearl!"));
                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
                
                EnderpearlHandler.stopCooldown(player);
                
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void a(ProjectileLaunchEvent event) {
	    if(!(event.getEntity() instanceof EnderPearl)) return;
	    if(!(event.getEntity().getShooter() instanceof Player)) return;

        EnderpearlHandler.applyCooldown((Player) event.getEntity().getShooter());
    }
    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if(event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            if(HCFConfiguration.kitMap) {
                event.setCancelled(true);
                return;
            }

            Location from = event.getFrom();
            Location to = event.getTo();
            Player player = event.getPlayer();

            Faction fromFac = RegisterHandler.getInstancee().getFactionManager().getFactionAt(from);
            
            if(fromFac.isSafezone()) {             	
				Location spawn = StringUtils.destringifyLocation(UtilitiesFile.configuration.getString("World-Spawn.nether-spawn"));
				
				if(spawn == null) {
					System.out.print("NETHER SPAWN IS NULL!");
					player.sendMessage(Color.translate("&c&lNether Spawn is not set please contact a Staff Member!"));
					return;
				}
				
				event.useTravelAgent(false);
				
				player.teleport(spawn);
                return;
            }

            if(event.useTravelAgent() && to.getWorld().getEnvironment() == World.Environment.NORMAL) {
                TravelAgent travelAgent = event.getPortalTravelAgent();
               
                if(!travelAgent.getCanCreatePortal()) return;

                Location foundPortal = travelAgent.findPortal(to);
                
                if(foundPortal != null) return; 

                Faction factionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(to);
                
                if(factionAt instanceof ClaimableFaction) {
                    Faction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);
                    
                    if(playerFaction != factionAt) {
                        player.sendMessage(Color.translate("&ePortal would have created portal in territory of " + factionAt.getDisplayName(player) + "&e."));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

        if(!event.isCancelled()) {
            Faction factionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(event.getLocation());

            if(((!factionAt.isSafezone()) || (reason != CreatureSpawnEvent.SpawnReason.SPAWNER)) && (event.getLocation().getWorld().getEnvironment() == World.Environment.NORMAL)) {
                int x = Math.abs(event.getLocation().getBlockX());
                int z = Math.abs(event.getLocation().getBlockZ());

                if((x < HCFConfiguration.warzoneRadius) && (z < HCFConfiguration.warzoneRadius)) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onEndCreatureSpawn(CreatureSpawnEvent event) {
    	if(event.getLocation().getWorld().getEnvironment() != Environment.THE_END) return;
    	
    	 if(!(event.getEntity() instanceof Slime)) return;
    	 if(!(event.getEntity() instanceof Creeper)) return;
    	 if(!(event.getEntity() instanceof Enderman)) return;
    	 
    	 event.setCancelled(true);
    }
    
	@EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if(entity instanceof Player) {
            Player player = (Player) entity;
            Faction playerFactionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(player.getLocation());
            EntityDamageEvent.DamageCause cause = event.getCause();
            if (playerFactionAt.isSafezone() && cause != EntityDamageEvent.DamageCause.SUICIDE && cause != EntityDamageEvent.DamageCause.VOID) {
                event.setCancelled(true);
            }

            Player attacker = HCFUtils.getFinalAttacker(event, true);
            
            if(attacker != null) {
                Faction attackerFactionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(attacker.getLocation());
              
                if(attackerFactionAt.isSafezone()) {
                    event.setCancelled(true);
                    return;
                } else if(playerFactionAt.isSafezone()) return;

                PlayerFaction attackerFaction;
                PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

                if(HCFData.getByName(player.getName()).isEvent()) {
                    return;
                }

                if(playerFaction != null && ((attackerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(attacker)) != null)) {
                    Role role = playerFaction.getMember(player).getRole();
                    String hiddenAstrixedName = role.getAstrix() + (player.hasPotionEffect(PotionEffectType.INVISIBILITY) ? "???" : player.getName());
                   
                    if(attackerFaction == playerFaction) {
                        attacker.sendMessage(Color.translate(HCFConfiguration.teammateColor + hiddenAstrixedName + " &eis in your faction!"));
                        
                        event.setCancelled(true);
                    } else if(attackerFaction.getAllied().contains(playerFaction.getUniqueID())) {
						event.setCancelled(true);
						
						attacker.sendMessage(Color.translate(HCFConfiguration.allyColor + hiddenAstrixedName + " &eis an ally!"));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        Entity entered = event.getEntered();
        
        if(entered instanceof Player) {
            Vehicle vehicle = event.getVehicle();
            
            if(vehicle instanceof Horse) {
                Horse horse = (Horse) event.getVehicle();
                AnimalTamer owner = horse.getOwner();
                
                if(owner != null && !owner.equals(entered)) {
                    ((Player) entered).sendMessage(Color.translate("&eYou can't enter a Horse that belongs to &c" + owner.getName() + "&e."));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Entity entity = event.getEntity();
       
        if(entity instanceof Player && ((Player) entity).getFoodLevel() > event.getFoodLevel() && RegisterHandler.getInstancee().getFactionManager().getFactionAt(entity.getLocation()).isSafezone()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getEntity();
       
        if(!HCFUtils.isDebuff(potion)) return;

        Faction factionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(potion.getLocation());
       
        if(factionAt.isSafezone()) {
            event.setCancelled(true);
            return;
        }

        ProjectileSource source = potion.getShooter();
        
        if(source instanceof Player) {
            Player player = (Player) source;
            
            for(LivingEntity affected : event.getAffectedEntities()) {
                if(affected instanceof Player && !player.equals(affected)) {
                    Player target = (Player) affected;
                    if(target.equals(source)) continue; 
                    
                    if(RegisterHandler.getInstancee().getFactionManager().getFactionAt(target.getLocation()).isSafezone()) {
                        event.setIntensity(affected, 0);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        switch(event.getReason()) {
        case CLOSEST_PLAYER:
        case RANDOM_TARGET:
            Entity target = event.getTarget();
            
            if(event.getEntity() instanceof LivingEntity && target instanceof Player) {
                Faction playerFaction; 
                Faction factionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(target.getLocation());
                
                if(factionAt.isSafezone() || ((playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction((Player) target)) != null && factionAt == playerFaction)) {
                    event.setCancelled(true);
                }
            }
            
            break;
        default:
            break;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!event.hasBlock()) return;

        Block block = event.getClickedBlock();
        Action action = event.getAction();
        if(action == Action.PHYSICAL) { 
            if(!attemptBuild(event.getPlayer(), block.getLocation(), null)) {
				if(HCFConfiguration.kitMap) {
					if(block.getLocation().getBlock().getType() == Material.STONE_PLATE
							|| block.getLocation().getBlock().getType() == Material.WOOD_PLATE
							|| block.getLocation().getBlock().getType() == Material.WORKBENCH
							|| block.getLocation().getBlock().getType() == Material.IRON_PLATE
							|| block.getLocation().getBlock().getType() == Material.GOLD_PLATE)
						return;
				}
				
				if(block.getType() == Material.PAPER) {
					event.setCancelled(false);
				}
				
				event.setCancelled(true);
            }
        } else if(action == Action.RIGHT_CLICK_BLOCK) {
            boolean canBuild = !BLOCK_INTERACTABLES.contains(block.getType());

            if(canBuild) {
                Material itemType = event.hasItem() ? event.getItem().getType() : null;
                
                if(itemType != null && ITEM_BLOCK_INTERACTABLES.containsKey(itemType) && ITEM_BLOCK_INTERACTABLES.get(itemType).contains(event.getClickedBlock().getType())) {
                    if(block.getType() != Material.WORKBENCH || !RegisterHandler.getInstancee().getFactionManager().getFactionAt(block).isSafezone()) {
                        canBuild = false;
                    }
                } else {
                    MaterialData materialData = block.getState().getData();
                   
                    if(materialData instanceof Cauldron) {
                        Cauldron cauldron = (Cauldron) materialData;
                        
                        if(!cauldron.isEmpty() && event.hasItem() && event.getItem().getType() == Material.GLASS_BOTTLE) {
                            canBuild = false;
                        }
                    }
                }
            }

            if(block.getType() != Material.WORKBENCH && !canBuild && !attemptBuild(event.getPlayer(), block.getLocation(), ChatColor.YELLOW + "You can't do this in the territory of %1$s" + ChatColor.YELLOW + '.', true)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        Faction factionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(event.getBlock().getLocation());
       
        if(factionAt instanceof WarzoneFaction || (factionAt instanceof Raidable && !((Raidable) factionAt).isRaidable())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        Faction factionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(event.getBlock().getLocation());
       
        if(factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeavesDelay(LeavesDecayEvent event) {
        Faction factionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(event.getBlock().getLocation());
        
        if(factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        Faction factionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(event.getBlock().getLocation());
        
        if(factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        
        if(entity instanceof LivingEntity && !attemptBuild(entity, event.getBlock().getLocation(), null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(!attemptBuild(event.getPlayer(), event.getBlock().getLocation(), Color.translate("&eYou can't break blocks in the territory of %1$s" + "&e!"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!attemptBuild(event.getPlayer(), event.getBlockPlaced().getLocation(), Color.translate("&eYou can't place blocks in the territory of %1$s" + "&e!"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        if(!attemptBuild(event.getPlayer(), event.getBlockClicked().getLocation(), Color.translate("&eYou can't build in the territory of %1$s" + "&e!"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if(!attemptBuild(event.getPlayer(), event.getBlockClicked().getLocation(), Color.translate("&eYou can't build in the territory of %1$s" + "&e!"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        Entity remover = event.getRemover();
        
        if(remover instanceof Player) {
            if(!attemptBuild(remover, event.getEntity().getLocation(), Color.translate("&eYou can't build in the territory of %1$s" + "&e!"))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        if(!attemptBuild(event.getPlayer(), event.getEntity().getLocation(), Color.translate("&eYou can't build in the territory of %1$s" + "&e!"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        
        if(entity instanceof Hanging) {
            Player attacker = HCFUtils.getFinalAttacker(event, false);
            
            if(!attemptBuild(attacker, entity.getLocation(), Color.translate("&eYou can't build in the territory of %1$s" + "&e!"))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onHangingInteractByPlayer(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
       
        if(entity instanceof Hanging) {
            if(!attemptBuild(event.getPlayer(), entity.getLocation(), Color.translate("&eYou can't build in the territory of %1$s" + "&e!"))) {
                event.setCancelled(true);
            }
        }
    }
    
    public static void handleMove(Player player, Location from, Location to, FactionPlayerClaimEnterEvent.EnterCause enterCause) {
        if(from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) return;

        boolean cancelled = false;

        Faction fromFaction = RegisterHandler.getInstancee().getFactionManager().getFactionAt(from);
        Faction toFaction = RegisterHandler.getInstancee().getFactionManager().getFactionAt(to);
        
        if(fromFaction != toFaction) {
            FactionPlayerClaimEnterEvent calledEvent = new FactionPlayerClaimEnterEvent(player, from, to, fromFaction, toFaction, enterCause);
            
            Bukkit.getPluginManager().callEvent(calledEvent);
            
            cancelled = calledEvent.isCancelled();
        } else if(toFaction instanceof CapturableFaction) {
            CapturableFaction capturableFaction = (CapturableFaction) toFaction;
            
            for(CaptureZone captureZone : capturableFaction.getCaptureZones()) {
                Cuboid cuboid = captureZone.getCuboid();
                
                if(cuboid != null) {
                    boolean containsFrom = cuboid.contains(from);
                    boolean containsTo = cuboid.contains(to);
                    
                    if(containsFrom && !containsTo) {
                        CaptureZoneLeaveEvent calledEvent = new CaptureZoneLeaveEvent(player, capturableFaction, captureZone);
                        
                        Bukkit.getPluginManager().callEvent(calledEvent);
                       
                        cancelled = calledEvent.isCancelled();
                        break;
                    } else if(!containsFrom && containsTo) {
                        CaptureZoneEnterEvent calledEvent = new CaptureZoneEnterEvent(player, capturableFaction, captureZone);
                        
                        Bukkit.getPluginManager().callEvent(calledEvent);
                        
                        cancelled = calledEvent.isCancelled();
                        break;
                    }
                }
            }
        }

        if(cancelled) {
            if(enterCause == FactionPlayerClaimEnterEvent.EnterCause.TELEPORT) {
                //event.setCancelled(true);
                return;
            } else {
				from.add(0.5, 0, 0.5);

				to = from;
            }
        }
    }
    
    public static boolean attemptBuild(Entity entity, Location location, String denyMessage) {
        return attemptBuild(entity, location, denyMessage, false);
    }

    public static boolean attemptBuild(Entity entity, Location location, String denyMessage, boolean isInteraction) {
        Player player = entity instanceof Player ? (Player) entity : null;

        if(player != null && player.getGameMode() == GameMode.CREATIVE && player.hasPermission(Permission.OP_PERMISSION)) return true;

        if(player != null && player.getWorld().getEnvironment() == World.Environment.THE_END) {
            player.sendMessage(Color.translate("&cYou can't build in the end!"));
            return false;
        }

        boolean result = false;
        
        Faction factionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(location);
        
        if(!(factionAt instanceof ClaimableFaction)) {
            result = true;
        } else if(factionAt instanceof Raidable && ((Raidable) factionAt).isRaidable()) {
            result = true;
        }

        if(player != null && factionAt instanceof PlayerFaction) {
            if(RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player) == factionAt) {
                result = true;
            }
        }
        
		if(result) {
			if(HCFConfiguration.kitMap) {
				if((!isInteraction) && (Math.abs(location.getBlockX()) <= 200) && (Math.abs(location.getBlockZ()) <= 200)) {
					if(denyMessage != null) {
						if(player.getGameMode() == GameMode.CREATIVE) {
							if(player.hasPermission(Permission.ADMIN_PERMISSION)) result = true;
						}
						
						player.sendMessage(Color.translate("&eYou may not build that close to the spawn."));
					}
					
					return false;
				}
			} else {
				if((!isInteraction) && (Math.abs(location.getBlockX()) <= 300) && (Math.abs(location.getBlockZ()) <= 300)) {
					if(denyMessage != null) {
						player.sendMessage(Color.translate("&eYou may not build that close to the spawn."));
					}
					
					return false;
				}
			}
		} else if(denyMessage != null && player != null) {
            player.sendMessage(String.format(denyMessage, factionAt.getDisplayName(player)));
        }

        return result;
    }

    public static boolean canBuildAt(Location from, Location to) {
        Faction toFactionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(to);
        
        return !(toFactionAt instanceof Raidable && !((Raidable) toFactionAt).isRaidable() && toFactionAt != RegisterHandler.getInstancee().getFactionManager().getFactionAt(from));
    }
}
