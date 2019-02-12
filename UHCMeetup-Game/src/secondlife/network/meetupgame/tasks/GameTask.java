package secondlife.network.meetupgame.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.data.GameData;
import secondlife.network.meetupgame.data.MeetupData;
import secondlife.network.meetupgame.managers.GameManager;
import secondlife.network.meetupgame.states.GameState;
import secondlife.network.meetupgame.states.PlayerState;
import secondlife.network.meetupgame.utilties.EloUtils;
import secondlife.network.meetupgame.utilties.MeetupUtils;
import secondlife.network.vituz.utilties.Msg;

/**
 * Created by Marko on 23.07.2018.
 */
public class GameTask extends BukkitRunnable {

    private MeetupGame plugin = MeetupGame.getInstance();

    public GameTask() {
        runTaskTimer(plugin, 20L, 20L);
    }

    @Override
    public void run() {
        GameData data = GameManager.getGameData();

        data.setGameTime(data.getGameTime() + 1);

        if(data.getGameTime() == 1500) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
        }

        if(plugin.getGameManager().getAlivePlayers() == 1) {
            MeetupData.getMeetupDatas().values().forEach(meetupData -> {
                if(meetupData != null && meetupData.getPlayerState().equals(PlayerState.PLAYING)) {
                    Bukkit.getOnlinePlayers().forEach(player -> player.setHealth(20.0));

                    int winnerElo = EloUtils.giveWinnerElo(meetupData);

                    meetupData.setElo(meetupData.getElo() + winnerElo);
                    meetupData.setWins(meetupData.getWins() + 1);
                    meetupData.setRerolls(meetupData.getRerolls() + 1);

                    data.setWinner(meetupData.getRealName() != null ? meetupData.getRealName() : meetupData.getName());
                    data.setGameState(GameState.WINNER);

                    Msg.sendMessage("&7&m-------------------------------------------");
                    Msg.sendMessage("");
                    Msg.sendMessage("&eCongratulations to &d" + data.getWinner() + " &efor winning the game.");
                    Msg.sendMessage("");
                    Msg.sendMessage("&d" + data.getWinner() + " &ehas gotten &d" + meetupData.getGameElo() + " &eelo with &d" + meetupData.getGameKills() + " &ekills.");
                    Msg.sendMessage("&eWinner Received &d" + winnerElo + " ELO&e, &d1 Win&e and &d1 Reroll &efor winning the game!");
                    Msg.sendMessage("");
                    Msg.sendMessage("&7&m-------------------------------------------");

                    new WinTask();
                    MeetupUtils.setMotd("&cEnding");
                    cancel();
                }
            });
        }
    }
}
