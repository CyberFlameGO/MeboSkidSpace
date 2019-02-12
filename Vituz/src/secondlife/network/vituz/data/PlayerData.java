package secondlife.network.vituz.data;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Marko on 09.05.2018.
 */

@Getter
@Setter
public class PlayerData {

    @Getter
    public static Map<String, PlayerData> dataMap = new HashMap<>();

    private String name;
    private String realName;
    private String group;
    private String color;
    private String prefix;
    private String worldTime;
    private String lastSeen;
    private String mainColor;
    private String secondColor;
    private String backLocation;

    private boolean frozen;
    private boolean socialSpy;
    private boolean sounds;
    private boolean toggleMsg;
    private boolean toggleChat;
    private boolean tab;

    private List<String> ignoring;
    private List<String> notes;

    private int filter;
    private int spam;
    private long chatDelay;
    private long silentSpam;
    private boolean god;

    private boolean loaded;

    public PlayerData(String name) {
        this.name = name;
        this.realName = name;

        this.group = "";
        this.color = "";
        this.prefix = "";
        this.worldTime = "DEFAULT";
        this.lastSeen = "";
        this.mainColor = "§5";
        this.secondColor = "§d";
        this.backLocation = "";

        this.frozen = false;
        this.socialSpy = false;
        this.sounds = true;
        this.toggleMsg = true;
        this.toggleChat = true;
        this.tab = true;

        this.ignoring = new ArrayList<>();
        this.notes = new ArrayList<>();

        this.filter = 0;
        this.spam = 0;
        this.chatDelay = 0L;
        this.god = false;

        this.loaded = false;

        dataMap.put(this.name, this);
    }

    public void save() {
        if(!loaded) return;

        Document document = new Document();

        document.put("name", this.name.toLowerCase());
        document.put("realName", this.name);

        Player player = Bukkit.getPlayer(this.name);

        if(player != null) {
            document.put("rank_name", VituzAPI.getRankName(player.getName()));
            document.put("last_seen", new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()));
        }

        document.put("color", this.color);
        document.put("prefix", this.prefix);
        document.put("world_time", this.worldTime);
        document.put("main_color", this.mainColor);
        document.put("second_color", this.secondColor);
        document.put("back_location", this.backLocation);

        document.put("frozen", this.frozen);
        document.put("social_spy", this.socialSpy);
        document.put("sounds", this.sounds);
        document.put("toggle_messages", this.toggleMsg);
        document.put("toggle_chat", this.toggleChat);
        document.put("tab", this.tab);

        document.put("ignoring", this.ignoring);
        document.put("notes", this.notes);

        this.loaded = false;

        dataMap.remove(this.name);

        Vituz.getInstance().getDatabaseManager().getEssData().replaceOne(Filters.eq("name", this.name.toLowerCase()), document, new UpdateOptions().upsert(true));
    }

    public void load() {
        Document document = (Document) Vituz.getInstance().getDatabaseManager().getEssData().find(Filters.eq("name", this.name.toLowerCase())).first();

        if (document != null) {
            this.realName = document.getString("realName");

            if(document.containsKey("rank_name")) {
                this.group = document.getString("rank_name");
            }

            Player player = Bukkit.getPlayer(this.name);

            if(player != null) {
                this.lastSeen = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date());
            } else if(document.containsKey("last_seen")) {
                this.lastSeen = document.getString("last_seen");
            }

            this.color = document.getString("color");
            this.prefix = document.getString("prefix");
            this.worldTime = document.getString("world_time");
            this.mainColor = document.getString("main_color");
            this.secondColor = document.getString("second_color");

            if(document.containsKey("back_location")) {
                this.backLocation = document.getString("back_location");
            }

            this.socialSpy = document.getBoolean("social_spy");
            this.sounds = document.getBoolean("sounds");
            this.toggleMsg = document.getBoolean("toggle_messages");
            this.toggleChat = document.getBoolean("toggle_chat");
            this.tab = document.getBoolean("tab");

            if(document.containsKey("frozen")) {
                this.frozen = document.getBoolean("frozen");
            }

            List<String> ignoreList = new ArrayList<>();

            for (String id : document.get("ignoring").toString().replace("[", "").replace("]", "").replace(" ", "").split(",")) {
                if (!id.isEmpty()) {
                    ignoreList.add(id);
                }
            }

            this.ignoring.addAll(ignoreList);

            List<String> noteList = new ArrayList<>();

            for (String id : document.get("notes").toString().replace("[", "").replace("]", "").replace(" ", "").split(",")) {
                if (!id.isEmpty()) {
                    noteList.add(id);
                }
            }

            this.notes.addAll(noteList);
        }

        this.loaded = true;
    }

    public static PlayerData getByName(String name) {
        PlayerData data = dataMap.get(name);

        return data == null ? new PlayerData(name) : data;
    }
}
