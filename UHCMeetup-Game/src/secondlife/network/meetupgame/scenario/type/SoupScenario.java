package secondlife.network.meetupgame.scenario.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import secondlife.network.meetupgame.scenario.Scenario;

public class SoupScenario extends Scenario implements Listener {

	public SoupScenario() {
		super("Soup", Material.MUSHROOM_SOUP, "When you right click a soup you regain 3.5 hearts.");
	}

	public static void handleInteract(Player player, ItemStack item, Action action, PlayerInteractEvent event) {
		if((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) {
			if(item == null || item.getType() != Material.MUSHROOM_SOUP) {
				return;
			}

			event.setCancelled(true);

			player.getItemInHand().setType(Material.BOWL);
			player.setHealth(player.getHealth() + 7.0D > player.getMaxHealth() ? player.getMaxHealth() : player.getHealth() + 7.0D);
		}
	}
}
