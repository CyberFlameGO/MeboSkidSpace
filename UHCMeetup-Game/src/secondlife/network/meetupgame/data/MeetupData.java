package secondlife.network.meetupgame.data;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import secondlife.network.meetupgame.states.PlayerState;
import secondlife.network.vituz.Vituz;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marko on 23.07.2018.
 */

@Getter
@Setter
public class MeetupData {

    @Getter
    public static Map<String, MeetupData> meetupDatas = new HashMap<>();

    private String name;
    private String realName;
    private String lastVoted;

    private int gameKills = 0;
    private int gameElo = 0;

    private int kills = 0;
    private int deaths = 0;
    private int elo = 1000;
    private int wins = 0;
    private int played = 0;
    private int highestKillStreak = 0;
    private int rerolls = 0;

    private PlayerState playerState = PlayerState.LOBBY;

    private boolean loaded;

    public MeetupData(String name) {
        this.name = name;

        meetupDatas.put(name, this);
    }

    public void save() {
        if(!loaded) return;

        Document document = new Document();
        document.put("name", name.toLowerCase());
        document.put("realName", name);
        document.put("kills", kills);
        document.put("deaths", deaths);
        document.put("elo", elo);
        document.put("wins", wins);
        document.put("played", played);
        document.put("highest_killstreak", highestKillStreak);

        document.put("rerolls", rerolls);

        Vituz.getInstance().getDatabaseManager().getMeetupProfiles().replaceOne(Filters.eq("name", this.name.toLowerCase()), document, new UpdateOptions().upsert(true));

        meetupDatas.remove(name);
        loaded = false;
    }

    public void load() {
        Document document = (Document) Vituz.getInstance().getDatabaseManager().getMeetupProfiles().find(Filters.eq("name", this.name.toLowerCase())).first();

        if(document != null) {
            this.realName = document.getString("realName");
            this.kills = document.getInteger("kills");
            this.deaths = document.getInteger("deaths");
            this.elo = document.getInteger("elo");
            this.wins = document.getInteger("wins");
            this.played = document.getInteger("played");
            this.highestKillStreak = document.getInteger("highest_killstreak");
            this.rerolls = document.getInteger("rerolls");
        }

        loaded = true;
    }

    public static MeetupData getByName(String name) {
        MeetupData data = meetupDatas.get(name);

        return data == null ? new MeetupData(name) : meetupDatas.get(name);
    }

    public boolean isAlive() {
        if(playerState.equals(PlayerState.PLAYING)) {
            return true;
        }

        return false;
    }

    public boolean isNotAlive() {
        if(playerState.equals(PlayerState.PLAYING)) {
            return false;
        }

        return true;
    }

    public double getKD() {
        double kd;

        if(this.kills > 0 && this.deaths == 0) {
            kd = this.kills;
        } else if(this.kills == 0 && this.deaths == 0) {
            kd = 0.0;
        } else {
            kd = this.kills / this.deaths;
        }

        return kd;
    }
}
