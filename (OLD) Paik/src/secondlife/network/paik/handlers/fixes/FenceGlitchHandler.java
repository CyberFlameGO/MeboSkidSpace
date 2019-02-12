package secondlife.network.paik.handlers.fixes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import secondlife.network.paik.Paik;
import secondlife.network.paik.utils.Handler;

public class FenceGlitchHandler extends Handler implements Listener {

	public static Material[] materials;
	
	public FenceGlitchHandler(Paik plugin) {
		super(plugin);

		materials = new Material[] { 
			    Material.POTION,
				Material.GOLDEN_APPLE,
				Material.DIAMOND_SWORD,
				Material.GOLD_SWORD,
				Material.IRON_SWORD,
				Material.STONE_SWORD,
				Material.WOOD_SWORD,
				Material.COOKED_BEEF,
				Material.RAW_BEEF,
				Material.COOKED_CHICKEN,
				Material.RAW_CHICKEN,
				Material.BAKED_POTATO,
				Material.GOLDEN_CARROT,
				Material.PORK,
				Material.GRILLED_PORK,
				Material.PUMPKIN_PIE,
				Material.BOW };
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.isCancelled()) return;
		
		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();
		
		if(item == null) return;
		
		Block block = event.getClickedBlock();
		
		if(block.getType() == Material.FENCE || block.getType() == Material.NETHER_FENCE || block.getType() == Material.CAULDRON) {
			for(Material material : materials) {
				if(item.getType() == material) {
					event.setCancelled(true);
				}
			}
		}
	}
}
