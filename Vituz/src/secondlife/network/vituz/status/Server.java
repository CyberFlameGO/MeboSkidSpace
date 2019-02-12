package secondlife.network.vituz.status;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
@AllArgsConstructor
public class Server {

    private String name;
    private boolean hub;

    public JsonObject getServerData() {
        JsonObject object = new JsonObject();
        object.addProperty("name", this.name);
        object.addProperty("motd", Bukkit.getMotd());
        object.addProperty("online-players", Bukkit.getOnlinePlayers().size());
        object.addProperty("maximum-players", Bukkit.getMaxPlayers());
        object.addProperty("whitelisted", Bukkit.hasWhitelist());
        object.addProperty("tps", Bukkit.spigot().getTPS()[0]);
        return object;
    }

}
