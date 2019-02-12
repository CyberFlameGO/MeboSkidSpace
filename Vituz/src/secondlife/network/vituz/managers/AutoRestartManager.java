package secondlife.network.vituz.managers;

import org.bukkit.Bukkit;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.utilties.Manager;

import java.util.Arrays;
import java.util.Date;

public class AutoRestartManager extends Manager {

    public AutoRestartManager(Vituz plugin) {
        super(plugin);

        check();
    }
    
    private void check() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () ->  {
            Date now = new Date();

            switch(VituzAPI.getServerName()) {
                case "Hub": {
                    if(now.getMinutes() == 1 && Arrays.asList(5, 11, 17, 23).contains(now.getHours())) {
                        VituzAPI.dispatchCommandOnMainThread("reboot 10m");
                    }
                    break;
                }
                case "UHCMeetup-Lobby": {
                    if(now.getMinutes() == 30 && Arrays.asList(5, 11, 17, 23).contains(now.getHours())) {
                        VituzAPI.dispatchCommandOnMainThread("reboot 10m");
                    }
                    break;
                }
                case "KitMap": {
                    if(now.getMinutes() == 20 && Arrays.asList(10, 22).contains(now.getHours())) {
                        VituzAPI.dispatchCommandOnMainThread("reboot 10m");
                    }
                    break;
                }
                case "UHC": {
                    if(now.getMinutes() == 10 && now.getHours() == 10) {
                        VituzAPI.dispatchCommandOnMainThread("reboot 20m");
                    }
                    break;
                }
            }
        }, 1200L, 1200L);
    }
}