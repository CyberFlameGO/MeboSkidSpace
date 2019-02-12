package secondlife.network.meetupgame.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.player.PlayerData;
import secondlife.network.meetupgame.state.GameState;
import secondlife.network.meetupgame.state.PlayerState;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.ServerUtils;

import java.util.Arrays;

/**
 * Created by Marko on 11.06.2018.
 */
public class GameTask extends BukkitRunnable {

    private MeetupGame plugin = MeetupGame.getInstance();
    public static int seconds = 0;

    public GameTask() {
        runTaskTimer(MeetupGame.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        // 1500 = 25min
        if(seconds > 1500) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                ServerUtils.sendToServer(player, "Minigames");
            }

            cancel();
            return;
        }

        seconds++;

        if(plugin.getGameManager().getAlivePlayers() == 1) {
            for(PlayerData data : PlayerData.getPlayerDatas()) {
                if(data.getPlayerState().equals(PlayerState.PLAYING)) {
                    data.setWins(data.getWins() + 1);

                    plugin.getGameManager().setGameState(GameState.WINNER);
                    plugin.getGameManager().setWinner(data.getName());

                    new WinTask();
                }
            }
        }
    }
}
