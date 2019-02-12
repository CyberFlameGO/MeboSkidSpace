package secondlife.network.meetupgame.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.data.GameData;
import secondlife.network.meetupgame.managers.GameManager;

/**
 * Created by Marko on 24.07.2018.
 */
public class BorderTimeTask extends BukkitRunnable {

    private MeetupGame plugin = MeetupGame.getInstance();
    public static int seconds = 60;

    public BorderTimeTask() {
		runTaskTimerAsynchronously(plugin, 20L, 20L);
    }

    @Override
    public void run() {
		GameData data = GameManager.getGameData();

        if(data.getBorder() <= 10) {
            data.setCanBorderTime(false);
            cancel();
            return;
        }

		if(data.isCanBorderTime()) {
			seconds--;
		} else {
			cancel();
		}
    }
}