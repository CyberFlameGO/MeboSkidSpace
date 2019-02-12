package secondlife.network.vituz.tasks;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.ServerUtils;

public class RebootTask extends BukkitRunnable {

	public static long seconds = 0;
	public static boolean running = false;
	public static RebootTask reboot;
	
	public RebootTask(long time) {
		this.runTaskTimerAsynchronously(Vituz.getInstance(), 20L, 20L);
		
		reboot = this;
		seconds = time;
		running = true;
		
		this.broadcast();
	}
	
	@Override
	public void run() {
		seconds-= 1000;
		
		if(seconds == 0) {
			this.restart();
			running = false;
			this.cancel();
		}
		
		if(seconds < 1000) return;
		
		if(seconds <= 10000 || seconds == 15000 || seconds == 30000 || seconds == 45000 || seconds == 60000 || seconds == 120000 || seconds == 180000 || seconds == 240000 || seconds == 300000 || seconds == 600000 || seconds == 900000 || seconds == 1200000 || seconds == 1500000 || seconds == 1800000) {
			this.broadcast();
		}
	}
	
	public void restart() {
		Msg.sendMessage(Color.translate("&4&lServer is rebooting..."));

		if(!VituzAPI.getServerName().equalsIgnoreCase("Hub")) {
			Bukkit.getOnlinePlayers().forEach(player -> {
				ServerUtils.sendToServer(player, "Hub");
				player.sendMessage(Color.translate("&eYou have been sent to hub due to server restart."));
			});
		}

		new BukkitRunnable() {
			public void run() {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
			}
		}.runTaskLater(Vituz.getInstance(), 20L);
	}
	
	public void broadcast() {
		Msg.sendMessage("&c⚠&4&m------------------------&c⚠");
		Msg.sendMessage(" &cServer rebooting in " + DurationFormatUtils.formatDurationWords(seconds, true, true));
		Msg.sendMessage("&c⚠&4&m------------------------&c⚠");
	}
	
	public void cancelRunnable() {
		cancel();
		
		Msg.sendMessage("&c⚠&4&m------------------------&c⚠");
		Msg.sendMessage(" &cServer reboot was canceled.");
		Msg.sendMessage("&c⚠&4&m------------------------&c⚠");
		
		running = false;
	}
}
