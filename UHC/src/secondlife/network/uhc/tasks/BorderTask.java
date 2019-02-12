package secondlife.network.uhc.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.managers.BorderManager;
import secondlife.network.uhc.managers.OptionManager;
import secondlife.network.uhc.utilties.UHCUtils;
import secondlife.network.vituz.utilties.Color;

import java.util.Arrays;

public class BorderTask extends BukkitRunnable {

    private UHC plugin = UHC.getInstance();
    private int seconds = (OptionManager.getByNameAndTranslate("Border Shrink Interval") * 60);

    @Override
    public void run() {
        if(BorderManager.border <= 25) {
            this.cancel();
            return;
        }
        
        if(!plugin.getGameManager().isBorderShrink()) {
            this.cancel();
        }

        seconds -= 10;
        
        if(seconds == 10) {
        	BorderManager.startSeconds();
        	
            this.cancel();
        } else if (seconds > 10) {
            if(Arrays.asList(600, 540, 480, 420, 360, 300, 240, 180, 120, 60).contains(seconds)) {
                Bukkit.broadcastMessage(Color.translate("&eBorder will shrink in &d" + (seconds / 60) + " &eminutes to &d" + UHCUtils.getNextBorder() + "&ex&d" + UHCUtils.getNextBorder() + "&e."));
            }
        }
    }
}