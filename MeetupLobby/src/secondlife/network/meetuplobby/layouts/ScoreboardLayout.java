package secondlife.network.meetuplobby.layouts;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.meetuplobby.MeetupLobby;
import secondlife.network.vituz.scoreboard.ScoreGetter;
import secondlife.network.vituz.scoreboard.ScoreboardConfiguration;
import secondlife.network.vituz.scoreboard.TitleGetter;
import secondlife.network.vituz.utilties.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marko on 11.06.2018.
 */
public class ScoreboardLayout implements ScoreGetter {

    private MeetupLobby plugin = MeetupLobby.getInstance();

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration sc = new ScoreboardConfiguration();

        sc.setTitleGetter(new TitleGetter("§5§lSecondLife"));
        sc.setScoreGetter(new ScoreboardLayout());

        return sc;
    }

    @Override
    public String[] getScores(Player player) {
        List<String> board = new ArrayList<>();
        add(board, "&7&m----------------------");

        int soloQueue = plugin.getQueueManager().getSoloQueue().size();
        int duoQueue = plugin.getQueueManager().getDuoQueue().size();
        int inLobby = Bukkit.getOnlinePlayers().size() - (soloQueue + duoQueue);

        add(board, "&fIn Lobby: &d" + inLobby);
        add(board, "&fSolo Queue: &d" + soloQueue);
        add(board, "&fDuo Queue: &d" + duoQueue);
        if(plugin.getQueueManager().isInQueue(player)) {
            add(board, "");
            add(board, "&a&o&lQueued for game...");
        }
        add(board, "");
        add(board, "&dsecondlife.network");
        add(board, "&9&7&m----------------------");

        return board.toArray(new String[] {});
    }

    private void add(List list, String text) {
        list.add(Color.translate(text));
    }
}
