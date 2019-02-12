package secondlife.network.info;

import lombok.Data;
import lombok.Getter;
import secondlife.network.vituz.status.ServerData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class MeetupData {

    @Getter
    private static Set<MeetupData> servers = new HashSet<>();

    private String name;
    private int remaining;
    private int initial;
    private int gameTime;
    private int border;
    private String scenario;

    public MeetupData(String name) {
        this.name = name;

        servers.add(this);
    }

    public static MeetupData getByID(int number) {
        for(MeetupData server : servers) {
            if(server.getName().equalsIgnoreCase("UHCMeetup-" + number)) {
                return server;
            }
        }

        return null;
    }

    public static MeetupData getByName(String name) {
        for(MeetupData server : servers) {
            if(server.getName().equalsIgnoreCase(name)) {
                return server;
            }
        }

        return null;
    }
}
