package secondlife.network.meetuplobby.utilities;

import lombok.Getter;
import secondlife.network.meetuplobby.MeetupLobby;

/**
 * Created by Marko on 10.06.2018.
 */
public class Manager {

    @Getter protected MeetupLobby plugin;

    public Manager(MeetupLobby plugin) {
        this.plugin = plugin;
    }
}
