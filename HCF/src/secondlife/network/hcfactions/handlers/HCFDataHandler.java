package secondlife.network.hcfactions.handlers;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.PlayerUtils;

public class HCFDataHandler extends Handler implements Listener {
	
	public HCFDataHandler(HCF plugin) {
		super(plugin);
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if(!PlayerUtils.isMongoConnected(event)) return;

		HCFData data = HCFData.getByName(event.getName());

		if (!data.isLoaded()) {
			data.load();
		}

		if(!data.isLoaded()) {
			PlayerUtils.kick(event);
		}
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		HCFData.getByName(event.getPlayer().getName()).save();
	}
}
