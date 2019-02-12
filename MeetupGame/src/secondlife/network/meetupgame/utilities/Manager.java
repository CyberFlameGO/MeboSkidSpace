package secondlife.network.meetupgame.utilities;

import lombok.Getter;
import secondlife.network.meetupgame.MeetupGame;

/**
 * Created by Marko on 11.06.2018.
 */
public class Manager {

    @Getter protected MeetupGame plugin;

    public Manager(MeetupGame plugin) {
        this.plugin = plugin;
    }
}
