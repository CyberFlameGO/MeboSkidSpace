package secondlife.network.hub.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import secondlife.network.hub.Hub;
import secondlife.network.hub.utilties.Manager;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Marko on 28.03.2018.
 */
public class AutoKickManager extends Manager implements Listener {

    private Map<UUID, Long> afk = new HashMap<>();

    public AutoKickManager(Hub plugin) {
        super(plugin);

        handleCheck();
    }

    public void handleMove(Player player) {
        if(afk.containsKey(player.getUniqueId())) {
            afk.remove(player.getUniqueId());
        }

        afk.put(player.getUniqueId(), System.currentTimeMillis() + (180 * 1000));
    }

    public void handleCheck() {
        Tasks.runTimer(() -> Bukkit.getOnlinePlayers().forEach(player -> {
            if(afk.containsKey(player.getUniqueId()) && afk.get(player.getUniqueId()) < System.currentTimeMillis() && QueueManager.getByPlayer(player) == null && !player.isOp()) {
                player.kickPlayer(Color.translate("&cYou were kicked because you were afk!"));
            }
        }), 20L, 20L);
    }

    public void handleRemove(Player player) {
        if(afk.containsKey(player.getUniqueId())) {
            afk.remove(player.getUniqueId());
        }
    }
}
