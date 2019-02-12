package secondlife.network.overpass.data;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.utilties.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marko on 10.05.2018.
 */

@Getter
@Setter
public class OverpassData {

    @Getter public static Map<String, OverpassData> profiles = new HashMap<>();

    private String group;
    private String name;
    private String email;
    private String code;
    private String password;
    private boolean register;
    private boolean needLogin;
    private boolean needToEnterCode;
    private boolean fullyRegistered;

    private boolean loaded;

    public OverpassData(String name) {
        this.name = name;
        this.email = "";
        this.code = "";
        this.password = "";
        this.register = false;
        this.needLogin = true;
        this.needToEnterCode = false;
        this.fullyRegistered = false;

        profiles.put(this.name, this);
    }

    public void save() {
        if(!loaded) return;

        Document document = new Document();
        document.put("name", name.toLowerCase());

        Player player = Bukkit.getPlayer(this.name);

        if(player != null) {
            document.put("rank_name", VituzAPI.getRankName(Bukkit.getPlayer(this.name).getName()));
        }

        document.put("email", this.email);
        document.put("code", this.code);
        document.put("password", this.password);

        document.put("register", this.register);
        document.put("needToEnterCode", this.needToEnterCode);
        document.put("fullyRegistered", this.fullyRegistered);

        Vituz.getInstance().getDatabaseManager().getAuthmeProfiles().replaceOne(Filters.eq("name", this.name.toLowerCase()), document, new UpdateOptions().upsert(true));

        profiles.remove(this.name);
    }

    public void load() {
        Document document = (Document) Vituz.getInstance().getDatabaseManager().getAuthmeProfiles().find(Filters.eq("name", this.name.toLowerCase())).first();

        if(document != null) {
            if(document.containsKey("rank_name")) {
                this.group = document.getString("rank_name");
            }
            this.email = document.getString("email");
            this.code = document.getString("code");
            this.password = document.getString("password");

            this.register = document.getBoolean("register");
            this.needToEnterCode = document.getBoolean("needToEnterCode");
            this.fullyRegistered = document.getBoolean("fullyRegistered");
        }

        this.loaded = true;
    }

    public static OverpassData getByName(String name) {
        OverpassData overpassData = profiles.get(name);

        return overpassData == null ? new OverpassData(name) : overpassData;
    }

    public void delete(CommandSender sender) {
        Document document = (Document) Vituz.getInstance().getDatabaseManager().getAuthmeProfiles().find(Filters.eq("name", this.name.toLowerCase())).first();

        if(document == null) {
            sender.sendMessage(Color.translate("&eThat player isn't registered!"));
            return;
        }

        this.email = "";
        this.code = "";
        this.password = "";
        this.register = false;
        this.needLogin = true;
        this.needToEnterCode = false;
        this.fullyRegistered = false;

        Player player = Bukkit.getPlayer(this.name);

        if(player != null) {
            player.kickPlayer(Color.translate("&cYou have been unregistered. Please re-join!"));
        } else {
            save();
        }

        sender.sendMessage(Color.translate("&eSuccessfully unregistered &d" + this.name));
    }
}
