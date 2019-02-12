package secondlife.network.hcfactions.utilties.redis;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class UUIDHandler implements Listener {
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		UUIDUtils.update(event.getPlayer().getUniqueId(), event.getPlayer().getName());
	}
}
