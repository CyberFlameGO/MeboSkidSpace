package secondlife.network.meetupgame.layouts;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.meetupgame.scenario.ScenarioManager;
import secondlife.network.meetupgame.tasks.StartingTask;
import secondlife.network.meetupgame.tasks.VoteTask;
import secondlife.network.meetupgame.tasks.WinTask;
import secondlife.network.vituz.scoreboard.ScoreGetter;
import secondlife.network.vituz.scoreboard.ScoreboardConfiguration;
import secondlife.network.vituz.scoreboard.TitleGetter;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marko on 12.06.2018.
 */
public class ScoreboardLayout implements ScoreGetter {

    private MeetupGame plugin = MeetupGame.getInstance();

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
        switch(plugin.getGameManager().getGameState()) {
            case VOTING:
                add(board, "&fOnline: &d" + Bukkit.getOnlinePlayers().size());
                add(board, "&fVoting ends in &d" + StringUtils.formatInt(VoteTask.seconds));
                add(board, "");
                add(board, "&5&lScenario votes");
                for(Scenario scenario : ScenarioManager.getScenarios()) {
                    add(board, "  &f- " + scenario.getName() + "&7: &d" + plugin.getVoteManager().getScenarioVotes().get(scenario.getName()));
                }
                break;
            case WAITING:
                add(board, "&fOnline: &d" + Bukkit.getOnlinePlayers().size());
                add(board, "&fStarting in &d" + StringUtils.formatInt(StartingTask.seconds));
            case WINNER:
                add(board, "&fWinner: &d" + plugin.getGameManager().getWinner());
                add(board, "&fStopping in &d" + StringUtils.formatInt(WinTask.seconds));
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
