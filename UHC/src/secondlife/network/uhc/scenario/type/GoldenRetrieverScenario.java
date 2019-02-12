package secondlife.network.uhc.scenario.type;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import secondlife.network.uhc.scenario.Scenario;
import secondlife.network.uhc.utilties.UHCUtils;

import java.util.List;

public class GoldenRetrieverScenario extends Scenario implements Listener {

	public GoldenRetrieverScenario() {
		super("Golden Retriever", Material.GOLDEN_APPLE, "Players drop 1 golden head on death.");
	}

	public static void handleDeath(List<ItemStack> drops) {
		drops.add(UHCUtils.getGoldenHead());
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.getDrops().add(UHCUtils.getGoldenHead());
	}

}
