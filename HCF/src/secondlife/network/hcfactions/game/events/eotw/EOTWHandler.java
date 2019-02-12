package secondlife.network.hcfactions.game.events.eotw;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.arguments.EOTWCommand;
import secondlife.network.hcfactions.factions.utils.enums.ClaimChangeEnum;
import secondlife.network.hcfactions.factions.utils.events.FactionClaimChangeEvent;
import secondlife.network.hcfactions.factions.utils.events.FactionCreateEvent;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class EOTWHandler extends Handler implements Listener {
	
	public static EOTWRunnable runnable;
	
	public EOTWHandler(HCF plugin) {
		super(plugin);
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if(!event.getMessage().toLowerCase().startsWith("/f ")) return;
		if(!event.getMessage().toLowerCase().startsWith("/faction ")) return;
		if(!EOTWCommand.eotwffa) return;
		
		Player player = event.getPlayer();
		
		if((player.hasPermission(Permission.OP_PERMISSION))) return;
		
		event.setCancelled(true);

		player.sendMessage(Color.translate("&cYou may not use this command in FFA!"));
	}
	
	@EventHandler
	public void onBlockBreae(BlockBreakEvent event) {
		if(event.isCancelled()) return;
		
		Player player = event.getPlayer();
		
		if(EOTWCommand.eotwffa) {
			if(player.hasPermission(Permission.OP_PERMISSION)) return;
			
			event.setCancelled(true);
		}
		
		if(player.getItemInHand() != null && player.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
			int amplifier = player.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) + 7;
			event.setExpToDrop(event.getExpToDrop() * amplifier);
		}
	}
	
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
		if(EOTWCommand.eotwffa) {
			if(player.hasPermission(Permission.OP_PERMISSION)) return;
			
			event.setCancelled(true);
		}
        
        if(player.getWorld().getEnvironment() != Environment.NETHER) return;
        if(!(event.getBlock().getState() instanceof CreatureSpawner)) return;
        if(player.hasPermission(Permission.ADMIN_PERMISSION)) return;

		event.setCancelled(true);
		
		player.sendMessage(Color.translate("&cYou can't place spawners in the nether."));   
    }
	
	@EventHandler
	public void onFactionCreate(FactionCreateEvent event) {
		if(!isEOTW()) return;
		
		if(!(event.getFaction() instanceof PlayerFaction)) return;
			
		event.setCancelled(true);
		
		event.getSender().sendMessage(Color.translate("&cYou can't create factions while &lEOTW &cis active."));
	}

    @EventHandler
    public void onFactionClaimChange(FactionClaimChangeEvent event) {
    	if(!isEOTW()) return;
        if(event.getCause() != ClaimChangeEnum.CLAIM) return;
            
		if(!(event.getClaimableFaction() instanceof PlayerFaction)) return;
		
		event.setCancelled(true);
		
		event.getSender().sendMessage(Color.translate("&cYou can't claim lands while &lEOTW &cis active."));
	}
    
    public static EOTWRunnable getRunnable() {
        return runnable;
    }
    
    public static boolean isEOTW() {
        return isEOTW(true);
    }
    
    public static void setEOTW(boolean yes) {
        if(yes == isEOTW(false)) return;

        if(yes) {
            runnable = new EOTWRunnable();
            
            runnable.runTaskTimer(HCF.getInstance(), 20L, 20L);
        } else {
            if(runnable != null) {
                runnable.cancel();
                
                runnable = null;
            }
        }
    }

    public static boolean isEOTW(boolean ignoreWarmup) {
        return runnable != null && (!ignoreWarmup || EOTWRunnable.getElapsedMilliseconds() > 0);
    }
}
