package secondlife.network.meetuplobby.party;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import secondlife.network.meetuplobby.utilities.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Party {

    private UUID leader;
    private List<OfflinePlayer> players;
    private List<UUID> invited;

    public Party() {
        this.players = new ArrayList<>();
        this.invited = new ArrayList<>();
    }

    public OfflinePlayer getLeaderPlayer() {
        for(OfflinePlayer offlinePlayer : this.players) {
            if(offlinePlayer.getUuid().equals(this.leader)) {
                return offlinePlayer;
            }
        }

        return null;
    }

    public boolean hasPlayer(UUID uuid) {
        for(OfflinePlayer offlinePlayer : this.players) {
            if(uuid.equals(this.leader)) {
                return true;
            }
        }

        return false;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("leader", this.leader.toString());

        JsonArray players = new JsonArray();

        for(OfflinePlayer offlinePlayer : this.players) {
            JsonObject player = new JsonObject();
            player.addProperty("username", offlinePlayer.getUsername());
            player.addProperty("uuid", offlinePlayer.getUuid().toString());
            players.add(player);
        }

        object.add("players", players);
        return object;
    }

}
