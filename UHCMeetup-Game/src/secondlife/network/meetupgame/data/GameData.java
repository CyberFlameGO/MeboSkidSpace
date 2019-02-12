package secondlife.network.meetupgame.data;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.meetupgame.states.GameState;

/**
 * Created by Marko on 23.07.2018.
 */

@Getter
@Setter
public class GameData {

    private int gameTime = 0;
    private int remaining = 0;
    private int initial = 0;
    private int border = 150;

    private int startingTime = 15;
    private int voteTime = 30;
    private int endTime = 10;

    private boolean canStartCountdown = false;
    private boolean canAnnounce = false;
    private boolean canBorderTime = false;
    private boolean generated = false;

    private String scenario = "None";
    private String winner = "vizualpevepe";

    private GameState gameState = GameState.VOTE;
}
