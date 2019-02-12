package secondlife.network.meetuplobby;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPubSub;
import secondlife.network.meetuplobby.jedis.JedisController;
import secondlife.network.meetuplobby.jedis.JedisPublisher;
import secondlife.network.meetuplobby.jedis.JedisSubscriber;
import secondlife.network.meetuplobby.managers.InventoryManager;
import secondlife.network.meetuplobby.managers.QueueManager;
import secondlife.network.meetuplobby.party.PartyManager;
import secondlife.network.meetuplobby.queue.QueueAction;
import secondlife.network.vituz.utilties.config.ConfigFile;

import java.util.UUID;

/**
 * Created by Marko on 10.06.2018.
 */

@Getter
public class MeetupLobby extends JavaPlugin {

    @Getter private static MeetupLobby instance;

    private JedisController controller;
    private JedisPublisher publisher;
    private JedisSubscriber subscriber;

    private InventoryManager inventoryManager;
    private PartyManager partyManager;
    private QueueManager queueManager;
    private RequestManager requestManager;
    
    private ConfigFile mainConfig;

    @Override
    public void onEnable() {
        instance = this;
        
        mainConfig = new ConfigFile(this, "config.yml");

        setupRedis();
    }

    @Override
    public void onDisable() {

    }
    
    private void setupRedis() {
        this.controller = new JedisController(mainConfig.getString("REDIS.HOST"), mainConfig.getBoolean("REDIS.AUTHENTICATE") ? mainConfig.getString("REDIS.PASSWORD") : null, mainConfig.getInt("REDIS.PORT"));
        this.publisher = new JedisPublisher(this.controller, "meetup-lobby-to-proxy");
        this.subscriber = new JedisSubscriber(this.controller, "meetup-lobby-to-bukkit") {
            @Override
            public JedisPubSub getConnection() {
                return new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        JsonObject object = new JsonParser().parse(message).getAsJsonObject();
                        JsonObject payload = object.get("payload").getAsJsonObject();
                        QueueAction action = QueueAction.valueOf(object.get("action").getAsString());

                        if(action == QueueAction.ADDED_PLAYER) {
                            JsonObject playerJson = payload.get("player").getAsJsonObject();

                            Player player = Bukkit.getPlayer(UUID.fromString(playerJson.get("uuid").getAsString()));

                            if(player != null && player.isOnline()) {
                                queueManager.addToQueue(player, true);
                            }
                        } else if(action == QueueAction.REMOVED_PLAYER) {
                            JsonObject playerJson = payload.get("player").getAsJsonObject();

                            Player player = Bukkit.getPlayer(UUID.fromString(playerJson.get("uuid").getAsString()));

                            if(player != null && player.isOnline()) {
                                queueManager.removeFromQueue(player, true);
                            }
                        } else if(action == QueueAction.ADDED_PARTY) {
                            JsonObject partyJson = payload.get("party").getAsJsonObject();
                            UUID leader = UUID.fromString(partyJson.get("leader").getAsString());

                            Player player = Bukkit.getPlayer(leader);

                            if(player != null && player.isOnline()) {
                                queueManager.addToQueue(player, false);
                            }
                        } else if (action == QueueAction.REMOVED_PARTY) {
                            JsonObject partyJson = payload.get("party").getAsJsonObject();
                            UUID leader = UUID.fromString(partyJson.get("leader").getAsString());

                            Player player = Bukkit.getPlayer(leader);

                            if(player != null && player.isOnline()) {
                                queueManager.removeFromQueue(player, false);
                            } else {
                                JsonArray playersJson = partyJson.get("players").getAsJsonArray();

                                for(int i = 0; i < playersJson.size(); i++) {
                                    JsonObject playerJson = playersJson.get(i).getAsJsonObject();

                                    Player partyPlayer = Bukkit.getPlayer(UUID.fromString(playerJson.get("uuid").getAsString()));

                                    if (partyPlayer != null && partyPlayer.isOnline()) {
                                        queueManager.removeFromQueue(partyPlayer, false);
                                    }
                                }
                            }
                        }
                    }
                };
            }
        };
    }
}
