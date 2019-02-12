package secondlife.network.hcfactions.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.Handler;

public class KitMapHandler extends Handler implements Listener {
	
	public KitMapHandler(HCF plugin) {
		super(plugin);
		
		Bukkit.getPluginManager().registerEvents(this, this.getInstance());
	}
		
	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		if(event.isCancelled()) return;

		Player player = event.getPlayer();
		ItemStack item = event.getItem();

		if(item.getType() == Material.POTION) {
			new BukkitRunnable() {
				public void run() {
					player.setItemInHand(new ItemStack(Material.AIR));
					
					player.updateInventory();
				}
			}.runTaskLater(this.getInstance(), 1L);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();
		Block block = event.getClickedBlock();
				
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(block.getType() == Material.ENDER_CHEST || block.getType() == Material.ANVIL || block.getType() == Material.ENCHANTMENT_TABLE) {
				event.setCancelled(true);
			}
			
			if(item == null || item.getType() == Material.AIR) return;
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();

		if(event.isCancelled()) return;
		if(block.getType() != Material.WEB) return;

		new BukkitRunnable() {
			public void run() {
				block.setType(Material.AIR);
			}
		}.runTaskLater(this.getInstance(), 200L);
	}

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
    	if(event.getEntity() instanceof Villager) return;
    	
		event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if(!RegisterHandler.getInstancee().getFactionManager().getFactionAt(event.getItemDrop().getLocation()).isSafezone()) return;
        
		event.getItemDrop().remove();
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if(!RegisterHandler.getInstancee().getFactionManager().getFactionAt(event.getLocation()).isSafezone()) return;
        
        event.getEntity().remove();
    }
}
