package secondlife.network.info.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import secondlife.network.info.jedis.JedisAction;
import secondlife.network.info.jedis.JedisSubscriptionHandler;
import secondlife.network.vituz.status.ServerData;

public class DataSubscriptionHandler implements JedisSubscriptionHandler {

    @Override
    public void handleMessage(JsonObject json) {
        JedisAction action = JedisAction.valueOf(json.get("action").getAsString());
        JsonObject data = json.get("data").isJsonNull() ? null : json.get("data").getAsJsonObject();

        switch (action) {
            case LIST: {
                for(JsonElement e : data.get("servers").getAsJsonArray()) {
                    JsonObject serverJson = e.getAsJsonObject();
                    String name = serverJson.get("name").getAsString();
                    ServerData serverData = ServerData.getByName(name);

                    if (serverData == null) {
                        serverData = new ServerData(name);
                    }

                    serverData.setMotd(serverJson.get("motd").getAsString());
                    serverData.setOnlinePlayers(serverJson.get("online-players").getAsInt());
                    serverData.setMaximumPlayers(serverJson.get("maximum-players").getAsInt());
                    serverData.setWhitelisted(serverJson.get("whitelisted").getAsBoolean());
                    serverData.setLastUpdate(serverJson.get("last-update").getAsLong());
                    serverData.setTps(serverJson.get("tps").getAsDouble());
                }
            }
            break;
        }
    }

}
