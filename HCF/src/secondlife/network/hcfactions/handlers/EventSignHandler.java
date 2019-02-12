package secondlife.network.hcfactions.handlers;

import org.apache.commons.lang.time.FastDateFormat;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EventSignHandler extends Handler implements Listener {
	
	public EventSignHandler(HCF plugin) {
		super(plugin);
		
		Bukkit.getPluginManager().registerEvents(this, this.getInstance());
	}

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if(!isEventSign(event.getBlock())) return;
        
		event.setCancelled(true);   
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        
        if(!isEventSign(block)) return;
        
		BlockState state = block.getState();
		Sign sign = (Sign) state;
		
		ItemStack stack = new ItemStack(Material.SIGN, 1);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&eEvent Sign"));
		meta.setLore(Arrays.asList(sign.getLines()));
		
		stack.setItemMeta(meta);

		Player player = event.getPlayer();
		World world = player.getWorld();
		
		if(player.getGameMode() != GameMode.CREATIVE && world.isGameRule("doTileDrops")) {
			world.dropItemNaturally(block.getLocation(), stack);
		}

		event.setCancelled(true);
		
		block.setType(Material.AIR);
		state.update();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack stack = event.getItemInHand();
        BlockState state = event.getBlock().getState();
        
        if(!(state instanceof Sign)) return;
        if(!stack.hasItemMeta()) return;
        
		ItemMeta meta = stack.getItemMeta();

		if(!meta.hasDisplayName()) return;
		if(!meta.getDisplayName().equals(Color.translate("&eEvent Sign"))) return;
		
		Sign sign = (Sign) state;
		List<String> lore = meta.getLore();

		int count = 0;
		for(String loreLine : lore) {
			sign.setLine(count++, loreLine);

			if(count == 4) break;
		}

		sign.update();
	}
    
    public static ItemStack getEventSign(String playerName, String kothName) {
		FastDateFormat date = FastDateFormat.getInstance("dd.MM HH:mm:ss", TimeZone.getTimeZone("Europe/Zagreb"), Locale.ENGLISH);

        return new ItemBuilder(Material.SIGN).name("&eEvent Sign").lore("&d" + kothName + " &ecaptured by &d" + playerName).lore("").lore("&d" + date.format(System.currentTimeMillis())).build();
    }
    
    private boolean isEventSign(Block block) {
        BlockState state = block.getState();
        
        if(state instanceof Sign) {
            String[] lines = ((Sign) state).getLines();
            
            return lines.length > 0 && lines[1] != null && lines[1].equals(Color.translate(" &ecaptured by "));
        }

        return false;
    }
}
