package secondlife.network.vituz.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.listeners.FreezeListener;
import secondlife.network.vituz.utilties.Color;

public class FreezeTask extends BukkitRunnable {
		
	public FreezeTask() {
		this.runTaskTimerAsynchronously(Vituz.getInstance(), 200L, 200L);
	}

	@Override
	public void run() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			PlayerData data = PlayerData.getByName(player.getName());

			if(data.isFrozen()) sendMessage(player);
		});
	}
	
	public void sendMessage(Player player) {
        player.sendMessage(Color.translate("&7&m---------------------------------------------------"));
        player.sendMessage(Color.translate(" &eYou have been frozen by a staff member"));
        player.sendMessage(Color.translate("   &eIf you disconnect you will be &4&lBANNED"));
        player.sendMessage(Color.translate("     &ePlease connect to our teamspeak"));
        player.sendMessage(Color.translate("          &7(ts.secondlife.network)"));
        player.sendMessage(Color.translate("           &eYou have &4&l3 minutes &eto join"));
        player.sendMessage(Color.translate("&7&m---------------------------------------------------"));
	}
}