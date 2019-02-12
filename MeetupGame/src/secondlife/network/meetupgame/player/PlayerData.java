package secondlife.network.meetupgame.player;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.state.PlayerState;
import secondlife.network.meetupgame.utilities.database.MeetupDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by Marko on 11.06.2018.
 */

@Getter
@Setter
public class PlayerData {

    @Getter private static Set<PlayerData> playerDatas = new HashSet<>();

    private UUID uuid;
    private PlayerState playerState = PlayerState.WAITING;
    private Map<String, Integer> votes = new HashMap<>();
    private String name;
    private int kills = 0;
    private int totalKills = 0;
    private int wins = 0;
    private int deaths = 0;
    private int killStreak = 0;
    private int played = 0;
    private int rerolls = 0;

    public PlayerData(UUID uuid, boolean cache) {
        this.uuid = uuid;

        load();

        if(cache) playerDatas.add(this);
    }

    public void saveAsync() {
        new BukkitRunnable() {
            public void run() {
                save();
            }
        }.runTaskAsynchronously(MeetupGame.getInstance());
    }

    public void save() {
        Document document = new Document();
        document.put("uuid", uuid.toString());

        if(this.name != null) {
            document.put("recentName", this.name);
            document.put("recentNameLowercase", this.name.toLowerCase());
        }

        document.put("kills", this.totalKills);
        document.put("wins", this.wins);
        document.put("deaths", this.deaths);
        document.put("kill_streak", this.killStreak);
        document.put("played", this.played);
        document.put("rerolls", this.rerolls);

        MeetupDatabase.profiles.replaceOne(Filters.eq("uuid", this.uuid.toString()), document, new UpdateOptions().upsert(true));
    }

    private void load() {
        Document document = (Document) MeetupDatabase.profiles.find(Filters.eq("uuid", uuid.toString())).first();

        if(document != null) {
            if(document.containsKey("recentName")) {
                this.name = document.getString("recentName");
            }

            if(document.containsKey("kills")) {
                this.totalKills = document.getInteger("kills");
            }

            if(document.containsKey("wins")) {
                this.wins = document.getInteger("wins");
            }

            if(document.containsKey("deaths")) {
                this.deaths = document.getInteger("deaths");
            }

            if(document.containsKey("kill_streak")) {
                this.killStreak = document.getInteger("kill_streak");
            }

            if(document.containsKey("played")) {
                this.played = document.getInteger("played");
            }

            if(document.containsKey("rerolls")) {
                this.rerolls = document.getInteger("rerolls");
            }
        }
    }

    private static PlayerData getByPlayerr(Player player) {
        for(PlayerData data : playerDatas) {
            if(data.getUuid().equals(player.getUniqueId())) return data;
        }

        return new PlayerData(player.getUniqueId(), true);
    }

    public static PlayerData getByPlayer(Player player) {
        if(player != null) return getByPlayerr(player);

        return new PlayerData(player.getUniqueId(), false);
    }

    public static PlayerData getByUuid(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        if(player != null) return getByPlayerr(player);

        return new PlayerData(uuid, false);
    }

    public static Pair<UUID, String> getExternalPlayerInformation(String name) throws IOException, ParseException {
        Document document = (Document) MeetupDatabase.profiles.find(Filters.eq("recentName", name)).first();

        if(document != null && document.containsKey("recentName")) {
            return new Pair(UUID.fromString(document.getString("uuid")), document.getString("recentName"));
        }

        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(reader.readLine());
        UUID uuid = UUID.fromString(String.valueOf(obj.get("id")).replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));

        name = String.valueOf(obj.get("name"));
        reader.close();

        return new Pair(uuid, name);
    }
}
