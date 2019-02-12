package secondlife.network.vituz.data;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import secondlife.network.vituz.Vituz;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marko on 09.05.2018.
 */

@Getter
@Setter
public class ChallengeData {

    @Getter public static Map<String, ChallengeData> dataMap = new HashMap<>();

    private String name;
    private String realName;
    private int points;

    // W 1
    private boolean w1_1;
    private boolean w1_2;
    private boolean w1_3;
    private boolean w1_4;
    private boolean w1_5;

    private int playedRankedMatches;
    private int wonRankedMatches;
    private int playedUHCGames;
    private int killsInSingleUHCGame;

    private boolean loaded;

    private ChallengeData(String name) {
        this.name = name;
        this.realName = name;
        this.points = 0;

        this.w1_1 = false;
        this.w1_2 = false;
        this.w1_3 = false;
        this.w1_4 = false;
        this.w1_5 = false;

        this.playedRankedMatches = 0;
        this.wonRankedMatches = 0;
        this.playedUHCGames = 0;
        this.killsInSingleUHCGame = 0;

        this.loaded = false;

        dataMap.put(this.name, this);
    }

    public void save() {
        if(!loaded) return;

        Document document = new Document();

        document.put("name", this.name.toLowerCase());
        document.put("realName", this.name);
        document.put("points", this.points);

        document.put("w1_1", this.w1_1);
        document.put("w1_2", this.w1_2);
        document.put("w1_3", this.w1_3);
        document.put("w1_4", this.w1_4);
        document.put("w1_5", this.w1_5);

        document.put("playedRankedMatches", this.playedRankedMatches);
        document.put("wonRankedMatches", this.wonRankedMatches);
        document.put("playedUHCGames", this.playedUHCGames);

        this.loaded = false;

        dataMap.remove(this.name);

        Vituz.getInstance().getDatabaseManager().getChallengeData().replaceOne(Filters.eq("name", this.name.toLowerCase()), document, new UpdateOptions().upsert(true));
    }

    public void load() {
        Document document = (Document) Vituz.getInstance().getDatabaseManager().getChallengeData().find(Filters.eq("name", this.name.toLowerCase())).first();

        if(document != null) {
            this.realName = document.getString("realName");
            this.points = document.getInteger("points");

            this.w1_1 = document.getBoolean("w1_1");
            this.w1_2 = document.getBoolean("w1_2");
            this.w1_3 = document.getBoolean("w1_3");
            this.w1_4 = document.getBoolean("w1_4");
            this.w1_5 = document.getBoolean("w1_5");

            this.playedRankedMatches = document.getInteger("playedRankedMatches");
            this.wonRankedMatches = document.getInteger("wonRankedMatches");
            this.playedUHCGames = document.getInteger("playedUHCGames");
        }

        this.loaded = true;
    }

    public static ChallengeData getByName(String name) {
        ChallengeData data = dataMap.get(name);

        return data == null ? new ChallengeData(name) : data;
    }
}
