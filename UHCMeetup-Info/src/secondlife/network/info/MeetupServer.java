package secondlife.network.info;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import secondlife.network.vituz.VituzAPI;

@Getter
@AllArgsConstructor
public class MeetupServer {

    private String name;
    private String scenario;
    private int remaining;
    private int initial;
    private int gameTime;
    private int border;

    public JsonObject getServerData() {
        JsonObject object = new JsonObject();
        if(!VituzAPI.getServerName().equals("UHCMeetup-Lobby")) {
            object.addProperty("name", this.name);
            object.addProperty("scenario", this.scenario);
            object.addProperty("remaining", this.remaining);
            object.addProperty("initial", this.initial);
            object.addProperty("game_time", this.gameTime);
            object.addProperty("border", this.border);
        }
        return object;
    }

}
