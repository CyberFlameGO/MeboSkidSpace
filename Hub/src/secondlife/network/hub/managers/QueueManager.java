package secondlife.network.hub.managers;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import secondlife.network.hub.Hub;
import secondlife.network.hub.data.QueueData;
import secondlife.network.hub.utilties.Manager;
import secondlife.network.vituz.status.ServerData;
import secondlife.network.vituz.utilties.Tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marko on 28.03.2018.
 */

public class QueueManager extends Manager implements Listener {

	@Getter
	private static List<QueueData> queues = new ArrayList<>();

	public QueueManager(Hub plugin) {
		super(plugin);
		
		queues = new ArrayList<>();

		queues.add(new QueueData("UHC-1"));
		queues.add(new QueueData("UHC-2"));
		queues.add(new QueueData("Factions"));
		queues.add(new QueueData("KitMap"));
		queues.add(new QueueData("UHCMeetup-Lobby"));

		Tasks.runTimer(() -> queues.forEach(queue -> {
            if(!queue.isPaused() && !queue.getPlayers().isEmpty() && getCount() < queue.getLimit()) {
                queue.sendFirst();
                queue.handleRemove(queue.getPlayerAt(0));
            }
        }), 30L, 30L);
	}
	
	public static QueueData getByPlayer(Player player) {
		for(QueueData queue : queues) {
			if(queue.getPlayers().contains(player)) return queue;
		}

		return null;
	}

	public static QueueData getByServer(String server) {
		for(QueueData queue : queues) {
			if(queue.getServer().equalsIgnoreCase(server)) return queue;
		}

		return null;
	}

	private int getCount() {
		for(QueueData queue : queues) {
			if(queue.getServer().equalsIgnoreCase("UHC-1")) {
				ServerData data = ServerData.getByName("UHC-1");

				if(data != null) {
					return data.getOnlinePlayers();
				}
			} else if(queue.getServer().equalsIgnoreCase("UHC-2")) {
				ServerData data = ServerData.getByName("UHC-2");

				if(data != null) {
					return data.getOnlinePlayers();
				}
			} else 	if(queue.getServer().equalsIgnoreCase("Factions")) {
				ServerData data = ServerData.getByName("Factions");

				if(data != null) {
					return data.getOnlinePlayers();
				}
			} else if(queue.getServer().equalsIgnoreCase("KitMap")) {
				ServerData data = ServerData.getByName("KitMap");

				if(data != null) {
					return data.getOnlinePlayers();
				}
			} else if(queue.getServer().equalsIgnoreCase("UHCMeetup-Lobby")) {
				ServerData data = ServerData.getByName("UHCMeetup-Lobby");

				if(data != null) {
					return data.getOnlinePlayers();
				}
			}
		}

		return 0;
	}

	public static String getQueueName(Player player) {
		return getByPlayer(player).getServer();
	}

	public void handleRemove(Player player) {
		queues.forEach(queue -> {
			if(queue.getPlayers().contains(player)) {
				queue.handleRemove(player);
			}

			if(queue.getTaskMap().containsKey(player)) {
				queue.getTaskMap().get(player).cancel();
				queue.getTaskMap().remove(player);
			}
		});
	}
}