package secondlife.network.paik.utilties.events.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class PlayerAlertEvent extends Event implements Cancellable {
    
    private static HandlerList HANDLER_LIST = new HandlerList();
    private AlertType alertType;
    private Player player;
    private String alert;
    private boolean cancelled;
    
    public static HandlerList getHandlerList() {
        return PlayerAlertEvent.HANDLER_LIST;
    }
    
    public HandlerList getHandlers() {
        return PlayerAlertEvent.HANDLER_LIST;
    }
    
    public PlayerAlertEvent(AlertType alertType, Player player, String alert) {
        this.alertType = alertType;
        this.player = player;
        this.alert = alert;
    }
    
    public enum AlertType {
        RELEASE, EXPERIMENTAL, DEVELOPMENT;
    }
}
