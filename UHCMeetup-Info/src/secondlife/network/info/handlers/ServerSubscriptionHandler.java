package secondlife.network.info.handlers;

import com.google.gson.JsonObject;
import secondlife.network.info.jedis.JedisAction;
import secondlife.network.info.jedis.JedisSubscriptionHandler;
import secondlife.network.vituz.status.ServerData;
import secondlife.network.vituz.utilties.Msg;

public class ServerSubscriptionHandler implements JedisSubscriptionHandler {

    public void handleMessage(JsonObject json) {
        JedisAction action = JedisAction.valueOf(json.get("action").getAsString());
        JsonObject data = json.get("data").isJsonNull() ? null : json.get("data").getAsJsonObject();

        switch (action) {
            case UPDATE: {
                String name = data.get("name").getAsString();
                ServerData serverData = ServerData.getByName(name);

                if(serverData == null) {
                    // Instantiate server data (which gets stored)

                    serverData = new ServerData(name);

                    Msg.logConsole("&aInitiated server data `" + name + "`");
                }

                serverData.setMotd(data.get("motd").getAsString());
                serverData.setOnlinePlayers(data.get("online-players").getAsInt());
                serverData.setMaximumPlayers(data.get("maximum-players").getAsInt());
                serverData.setWhitelisted(data.get("whitelisted").getAsBoolean());
                serverData.setLastUpdate(System.currentTimeMillis());
                serverData.setTps(data.get("tps").getAsDouble());
            }
            break;
        }
    }
}
