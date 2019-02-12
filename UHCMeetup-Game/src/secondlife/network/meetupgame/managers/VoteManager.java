package secondlife.network.meetupgame.managers;

import lombok.Getter;
import org.bukkit.entity.Player;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.data.MeetupData;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.meetupgame.utilties.Manager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marko on 13.06.2018.
 */

@Getter
public class VoteManager extends Manager {

    private Map<Scenario, Integer> votes = new HashMap<>();

    public VoteManager(MeetupGame plugin) {
        super(plugin);

        ScenarioManager.getScenarios().forEach(scenario -> votes.put(scenario, 0));
    }

    Scenario getHighestVote() {
        Scenario highestScenario = null;
        int highestVote = 0;

        for(Map.Entry<Scenario, Integer> entry : votes.entrySet()) {
            if(entry.getValue() > highestVote) {
                highestScenario = entry.getKey();
                highestVote = entry.getValue();
            }
        }

        return highestScenario == null ? ScenarioManager.getByName("Default") : highestScenario;
    }

    public void handleAddVote(Player player, Scenario scenario) {
        votes.put(scenario, votes.get(scenario) + 1);
        MeetupData.getByName(player.getName()).setLastVoted(scenario.getName());
    }

    public void handleRemove(Player player, Scenario newVote) {
        MeetupData data = MeetupData.getByName(player.getName());
        Scenario scenario = ScenarioManager.getByName(data.getLastVoted());

        votes.put(scenario, votes.get(scenario) - 1);
        data.setLastVoted(null);

        handleAddVote(player, newVote);
    }

    public boolean hasVoted(Player player) {
        MeetupData data = MeetupData.getByName(player.getName());

        return data.getLastVoted() != null;
    }
}