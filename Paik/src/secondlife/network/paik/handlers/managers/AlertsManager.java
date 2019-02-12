package secondlife.network.paik.handlers.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.utilties.Handler;
import secondlife.network.vituz.utilties.Color;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AlertsManager extends Handler {
    
    private Set<UUID> devAlertsToggled = new HashSet<>();
    private Set<UUID> alertsToggled = new HashSet<>();

    public AlertsManager(Paik plugin) {
        super(plugin);
    }

    public boolean hasAlertsToggled(Player player) {
        return this.alertsToggled.contains(player.getUniqueId());
    }

    public boolean hasDevAlertsToggled(Player player) {
        return this.devAlertsToggled.contains(player.getUniqueId());
    }

    public void toggleAlerts(Player player) {
        if(!this.alertsToggled.remove(player.getUniqueId())) {
            this.alertsToggled.add(player.getUniqueId());
        }
    }
    
    public void forceAlert(String message) {
        for(UUID uuid : this.alertsToggled) {
            Player player = Bukkit.getPlayer(uuid);

            if(player != null) {
                player.sendMessage(Color.translate(message));
            }
        }
    }
}
