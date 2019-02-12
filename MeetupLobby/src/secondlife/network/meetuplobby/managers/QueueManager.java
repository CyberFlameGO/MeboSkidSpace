package secondlife.network.meetuplobby.managers;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.entity.Player;
import secondlife.network.meetuplobby.MeetupLobby;
import secondlife.network.meetuplobby.party.Party;
import secondlife.network.meetuplobby.queue.QueueAction;
import secondlife.network.meetuplobby.utilities.Manager;
import secondlife.network.vituz.utilties.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marko on 10.06.2018.
 */

@Getter
public class QueueManager extends Manager {

    private List<UUID> soloQueue = new ArrayList<>();
    private List<UUID> duoQueue = new ArrayList<>();

    public QueueManager(MeetupLobby plugin) {
        super(plugin);
    }

    public void addToQueue(Player player, boolean solo) {
        if(solo) {
            if(!isInQueue(player)) {
                JsonObject object = new JsonObject();
                object.addProperty("action", QueueAction.ADD_PLAYER.name());

                JsonObject playerJson = new JsonObject();
                playerJson.addProperty("username", player.getName());
                playerJson.addProperty("uuid", player.getUniqueId().toString());

                JsonObject payload = new JsonObject();
                payload.add("player", playerJson);

                object.add("payload", payload);

                plugin.getPublisher().write(object.toString());
                soloQueue.add(player.getUniqueId());
            }
        } else {
            Party party = plugin.getPartyManager().getByUuid(player.getUniqueId());

            if(party.getLeader().equals(player.getUniqueId())) {
                JsonObject object = new JsonObject();
                object.addProperty("action", QueueAction.ADD_PARTY.name());

                JsonObject payload = new JsonObject();
                payload.add("party", party.toJson());

                object.add("payload", payload);

                Bunkers.getInstance().getPublisher().write(object.toString());
            } else {
                player.sendMessage(ChatColor.RED + "You must be the party leader to do that.");
            }
        }
    }

    public void removeFromQueue(Player player, boolean solo) {
        if(solo) {
            if(soloQueue.contains(player.getUniqueId())) {
                soloQueue.remove(player.getUniqueId());
            }
        } else {
            Party party = plugin.getPartyManager().getByUuid(player.getUniqueId());

            party.getPlayers().forEach(players -> {
                if(duoQueue.contains(players.getUuid())) {
                    duoQueue.remove(players.getUuid());
                }
            });
        }
    }

    public boolean isInQueue(Player player) {
        if(soloQueue.contains(player.getUniqueId()) || duoQueue.contains(player.getUniqueId())) {
            return true;
        }

        return false;
    }
}
