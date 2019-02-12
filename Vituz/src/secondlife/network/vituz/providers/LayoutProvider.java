package secondlife.network.vituz.providers;

import org.bukkit.entity.Player;
import secondlife.network.vituz.providers.tab.TabLayout;

public interface LayoutProvider {
	
	TabLayout getLayout(Player player);
}
