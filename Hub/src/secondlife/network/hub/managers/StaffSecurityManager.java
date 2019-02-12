package secondlife.network.hub.managers;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import secondlife.network.hub.Hub;
import secondlife.network.hub.data.StaffData;
import secondlife.network.hub.utilties.Manager;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class StaffSecurityManager extends Manager {

	private List<UUID> users = new ArrayList<>();
	
	public StaffSecurityManager(Hub plugin) {
		super(plugin);

		handleSendMessage();
	}

	private void handleSendMessage() {
		Tasks.runTimer(() -> Bukkit.getOnlinePlayers().forEach(player -> {
            if(users.contains(player.getUniqueId())) {
                StaffData data = StaffData.getByName(player.getName());

                if(data.getPassword().equalsIgnoreCase("")) {
                    player.sendMessage(Color.translate("&cPlease register using /securityregister <password>"));
                } else {
                    player.sendMessage(Color.translate("&cPlease login using /auth <password>"));
                }
            }
        }), 50L, 50L);
	}

	public void handleRemove(Player player) {
		if(users.contains(player.getUniqueId())) {
			users.remove(player.getUniqueId());
		}
	}

	public void handleMove(Player player, Location from, Location to) {
		if(!users.contains(player.getUniqueId())) {
			return;
		}

		player.teleport(from);

		if(from.getX() != to.getX() || from.getZ() != to.getZ()) {
			player.teleport(from);
		}
	}
}
