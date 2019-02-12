package secondlife.network.hcfactions.factions.claim;

import com.google.common.base.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.visualise.VisualBlock;
import secondlife.network.vituz.visualise.VisualType;
import secondlife.network.vituz.visualise.VisualiseHandler;

import java.util.ArrayList;
import java.util.List;

public class ClaimWandHandler extends Handler implements Listener {
	
	public ClaimWandHandler(HCF plugin) {
		super(plugin);
		
		Bukkit.getPluginManager().registerEvents(this, this.getInstance());
	}
	
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();

        if(action == Action.PHYSICAL || !event.hasItem() || !isClaimingWand(event.getItem())) return;

        Player player = event.getPlayer();
        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player.getName());

        if(player.isSneaking()) {
            if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
                ClaimSelection claimSelection = ClaimHandler.claimSelectionMap.get(player.getUniqueId());

                if (claimSelection == null || !claimSelection.hasBothPositionsSet()) {
                    player.sendMessage(Color.translate("&cYou haven't set both positions of this claim selection."));
                    return;
                }


                if (ClaimHandler.tryPurchasing(player, claimSelection.toClaim(playerFaction))) {
                    ClaimHandler.clearClaimSelection(player);

                    player.setItemInHand(new ItemStack(Material.AIR, 1));
                }

                return;
            } else if(action == Action.RIGHT_CLICK_AIR) {
                ClaimHandler.clearClaimSelection(player);

                player.setItemInHand(new ItemStack(Material.AIR, 1));
                player.sendMessage(Color.translate("&eYou have cleared your &dClaim&e selection."));
                return;
            }
        }

        if(action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            Location blockLocation = block.getLocation();

            if(action == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
            }

            if(ClaimHandler.canClaimHere(player, blockLocation)) {
                ClaimSelection revert;
                ClaimSelection claimSelection = ClaimHandler.claimSelectionMap.putIfAbsent(player.getUniqueId(), revert = new ClaimSelection(blockLocation.getWorld()));
                
                if(claimSelection == null) claimSelection = revert;

                Location oldPosition;
                Location opposite;
                
                int selectionId;
                
                switch(action) {
                case LEFT_CLICK_BLOCK:
                    oldPosition = claimSelection.getPos1();
                    opposite = claimSelection.getPos2();
                    
                    selectionId = 1;
                    break;
                case RIGHT_CLICK_BLOCK:
                    oldPosition = claimSelection.getPos2();
                    opposite = claimSelection.getPos1();
                    
                    selectionId = 2;
                    break;
                default:
                    return; 
                }

                int blockX = blockLocation.getBlockX();
                int blockZ = blockLocation.getBlockZ();
                
                if(oldPosition != null && blockX == oldPosition.getBlockX() && blockZ == oldPosition.getBlockZ()) return;
                
                if((System.currentTimeMillis() - claimSelection.getLastUpdateMillis()) <= ClaimHandler.PILLAR_BUFFER_DELAY_MILLIS) return;

                if (opposite != null) {
                    int xDiff = Math.abs(opposite.getBlockX() - blockX) + 1; 
                    int zDiff = Math.abs(opposite.getBlockZ() - blockZ) + 1; 
                    
                    if(xDiff < ClaimHandler.MIN_CLAIM_RADIUS || zDiff < ClaimHandler.MIN_CLAIM_RADIUS) {
                        player.sendMessage(Color.translate("&cClaim selections must be at least &l" + ClaimHandler.MIN_CLAIM_RADIUS + 'x' + ClaimHandler.MIN_CLAIM_RADIUS + " &cblocks."));
                        return;
                    }
                }

                if(oldPosition != null) {
					VisualiseHandler.clearVisualBlocks(player, VisualType.PURPLE, visualBlock -> {
                        Location location = visualBlock.getLocation();

                        return location.getBlockX() == oldPosition.getBlockX() && location.getBlockZ() == oldPosition.getBlockZ();
                    });
                }

                if(selectionId == 1) claimSelection.setPos1(blockLocation);
                if(selectionId == 2) claimSelection.setPos2(blockLocation);

                player.sendMessage(Color.translate("&eSet the location of claim selection &d" + selectionId + " &eto: &d" + '(' +  blockX + ", " + blockZ + ')'));

                if(claimSelection.hasBothPositionsSet()) {
                    ClaimZone claim = claimSelection.toClaim(playerFaction);
                    
                    int selectionPrice = claimSelection.getPrice(playerFaction, false);
                    
                    player.sendMessage(Color.translate("&eClaim selection cost: &d$" + (selectionPrice > playerFaction.getBalance() ? ChatColor.RED : ChatColor.GREEN) + selectionPrice + "&e. Current size: &7(&d" + claim.getWidth() + "&7, &d" + claim.getLength() + "&7), &d" + claim.getArea() + " &eblocks."));
                }

                int blockY = block.getY();
                int maxHeight = player.getWorld().getMaxHeight();
                
                List<Location> locations = new ArrayList<>(maxHeight);
                
                for(int i = blockY; i < maxHeight; i++) {
                    Location other = blockLocation.clone();
                    
                    other.setY(i);
                    locations.add(other);
                }

                new BukkitRunnable() {
                    public void run() {
                        VisualiseHandler.generate(player, locations, VisualType.PURPLE, true);
                    }
                }.runTask(this.getInstance());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(isClaimingWand(event.getPlayer().getItemInHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            if(isClaimingWand(player.getItemInHand())) {
                player.setItemInHand(new ItemStack(Material.AIR, 1));
                ClaimHandler.clearClaimSelection(player);
            }
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        event.getPlayer().getInventory().remove(ClaimHandler.CLAIM_WAND);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().getInventory().remove(ClaimHandler.CLAIM_WAND);
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();

        if(isClaimingWand(item.getItemStack())) {
            item.remove();
            ClaimHandler.clearClaimSelection(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        Item item = event.getItem();

        if(isClaimingWand(item.getItemStack())) {
            item.remove();
            ClaimHandler.clearClaimSelection(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(event.getDrops().remove(ClaimHandler.CLAIM_WAND)) {
            ClaimHandler.clearClaimSelection(event.getEntity());
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        HumanEntity humanEntity = event.getPlayer();

        if(humanEntity instanceof Player) {
            Player player = (Player) humanEntity;

            if(player.getInventory().contains(ClaimHandler.CLAIM_WAND)) {
                player.getInventory().remove(ClaimHandler.CLAIM_WAND);
                ClaimHandler.clearClaimSelection(player);
            }
        }
    }

    public boolean isClaimingWand(ItemStack stack) {
        return stack != null && stack.isSimilar(ClaimHandler.CLAIM_WAND);
    }
}