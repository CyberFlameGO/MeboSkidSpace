package secondlife.network.meetupgame.managers;

import lombok.Getter;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.utilities.Manager;

import java.util.*;

/**
 * Created by Marko on 13.06.2018.
 */

@Getter
public class VoteManager extends Manager {

    private Map<String, Integer> scenarioVotes = new HashMap<>();
    private List<UUID> users = new ArrayList<>();

    public VoteManager(MeetupGame plugin) {
        super(plugin);
    }

    public String getHighestVote() {
        String highestScenario = null;
        int highestVote = 0;

        for(Map.Entry<String, Integer> entry : scenarioVotes.entrySet()) {
            if(entry.getValue() > highestVote) {
                highestScenario = entry.getKey();
                highestVote = entry.getValue();
            }
        }

        return highestScenario;
    }
}
