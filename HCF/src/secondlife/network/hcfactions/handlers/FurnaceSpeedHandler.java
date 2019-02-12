package secondlife.network.hcfactions.handlers;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.utilties.Handler;

public class FurnaceSpeedHandler extends Handler implements Listener {

	public FurnaceSpeedHandler(HCF plugin) {
		super(plugin);
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
    
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			
			if(!(block.getState() instanceof Furnace)) return;
			
			Furnace furnace = (Furnace) block.getState();
			
			furnace.setCookTime((short) (furnace.getCookTime() + 2));
		}
	}
    
    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        BlockState blockState = event.getBlock().getState();
        
        if(!(blockState instanceof Furnace)) return;
        
		Furnace furnace = (Furnace) blockState;

		if(2 > 1) {
			new FurnaceUpdateTask(furnace).runTaskTimer(this.getInstance(), 2L, 2L);
		}
    }
    
    public class FurnaceUpdateTask extends BukkitRunnable {
        
    	private Furnace furnace;
        
        public FurnaceUpdateTask(Furnace furnace) {
            this.furnace = furnace;
        }
        
        public void run() {
            this.furnace.setCookTime((short)(this.furnace.getCookTime() + 2));
            this.furnace.update();

            if(this.furnace.getBurnTime() <= 1) this.cancel();
        }
    }
}
