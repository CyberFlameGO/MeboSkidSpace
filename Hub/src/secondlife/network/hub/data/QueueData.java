package secondlife.network.hub.data;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import secondlife.network.hub.Hub;
import secondlife.network.hub.utilties.HubUtils;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Tasks;

import java.util.*;

/**
 * Created by Marko on 28.03.2018.
 */

@Getter
@Setter
public class QueueData {
	
	private String server;
	private List<Player> players;
	private Map<Player, BukkitTask> taskMap;
	private boolean paused;
	private int limit;
	
	public QueueData(String server) {
		this.server = server;
		this.players = new ArrayList<>();
		this.taskMap = new HashMap<>();
		this.paused = false;
		this.limit = 1000;

		Bukkit.getScheduler().runTaskTimer(Hub.getInstance(), () -> players.forEach(player -> {
            if(player.isOnline()) {
                player.sendMessage("");
                player.sendMessage(Color.translate("&eYou are &d" + (players.indexOf(player) + 1) + " &ein the &d" + server + " &equeue."));
                player.sendMessage(Color.translate("&eSkip the queue by purchasing a rank at &dstore.secondlife.network"));
                player.sendMessage("");
            } else {
                players.remove(player);
            }
        }), 15*20L, 15*20L);
	}

	public void handlePut(Player player) {
		if(Hub.getInstance().getStaffSecurityManager().getUsers().contains(player.getUniqueId())) return;
		if(players.contains(player)) return;
		
		if(HubUtils.getPriority(player) == 0) {
			if(VituzAPI.isBanned(player)) {
				player.sendMessage(Color.translate("&cYour account is currently suspended. To appeal, visit " + Vituz.getInstance().getEssentialsManager().getAppealAt() + " to appeal!"));
			} else {
				sendDirect(player);
				player.sendMessage(Color.translate("&aYou have been sent to " + this.server + "."));
			}

			return;
		}
		
		players.add(player);

		for(Player users : this.players) {
			int pos = players.indexOf(users);

			if(users != player) {
				if(HubUtils.getPriority(player) < HubUtils.getPriority(users)) {
					if(players.get(pos).isOnline()) {
						players.get(pos).sendMessage(Color.translate("&eSomeone with higher queue priority has joined the queue!"));
					}

					Collections.swap(players, pos, players.size() - 1);
				}
			}
		}
	}
	
	public void handleRemove(Player player) {
		if(!players.contains(player)) return;
		
		players.remove(player);
	}
	
	public Player getPlayerAt(int player) {
		return players.get(player);
	}

	public void sendFirst() {
		if(!players.isEmpty()) {
			Player p = players.get(0);

			ByteArrayDataOutput out = ByteStreams.newDataOutput();

			out.writeUTF("Connect");
			out.writeUTF(server);

			p.sendPluginMessage(Hub.getInstance(), "BungeeCord", out.toByteArray());
		}
	}
	
	public void sendDirect(Player player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();

		out.writeUTF("Connect");
		out.writeUTF(server);

		player.sendPluginMessage(Hub.getInstance(), "BungeeCord", out.toByteArray());
	}
}
