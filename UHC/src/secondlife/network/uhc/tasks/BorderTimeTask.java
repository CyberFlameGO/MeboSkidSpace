package secondlife.network.uhc.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.managers.BorderManager;

public class BorderTimeTask extends BukkitRunnable {

	private UHC plugin = UHC.getInstance();
	public static int seconds = 0;
    
    public BorderTimeTask() {
    	this.runTaskTimerAsynchronously(UHC.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        if(BorderManager.border <= 25) {
            this.cancel();
            return;
        }
    	
		if(plugin.getGameManager().isBorderTime()) {
			seconds--;
		} else {
			this.cancel();
		}
    }

	public static void setSeconds() {
		seconds = 300;
	}
}