package secondlife.network.vituz.providers.threads;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.providers.tab.VituzTab;

public class TabThread extends Thread {

	public TabThread() {
		super("Vituz - Tab Thread");
		setDaemon(false);
	}

	@Override
	public void run() {
		while(true) {
			for(Player online : Bukkit.getOnlinePlayers()) {
				try {
					if(VituzAPI.hasEssentialsData(online)) {
						VituzTab.updatePlayer(online);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
		 	} try {
				Thread.sleep(VituzAPI.tabTime * 50L);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
