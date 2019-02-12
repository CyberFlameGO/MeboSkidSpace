package secondlife.network.meetupgame.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.managers.GameManager;
import secondlife.network.meetupgame.utilties.MeetupUtils;
import secondlife.network.vituz.utilties.Color;

import java.util.Arrays;

public class BorderTask extends BukkitRunnable {

    private int i = 60;

    @Override
    public void run() {
        if(GameManager.getGameData().getBorder() <= 10) {
            cancel();
            return;
        }

        i -= 10;
        
        if(i == 10) {
            MeetupGame.getInstance().getBorderManager().handleStartSeconds();
            cancel();
        } else if(i > 10) {
            if(Arrays.asList(55, 45, 30, 25, 20, 15).contains(i)) {
                Bukkit.broadcastMessage(Color.translate("&eBorder will shrink in &d" + i + " &eseconds to &d" + MeetupUtils.getNextBorder() + "&ex&d" + MeetupUtils.getNextBorder() + "&e."));
            }
        }
    }
}