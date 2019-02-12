package secondlife.network.paik.utilties.events.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class PlayerBanEvent extends Event implements Cancellable {
    
    private static HandlerList HANDLER_LIST = new HandlerList();
    private Player player;
    private String reason;
    private boolean cancelled;
    
    public static HandlerList getHandlerList() {
        return PlayerBanEvent.HANDLER_LIST;
    }
    
    public HandlerList getHandlers() {
        return PlayerBanEvent.HANDLER_LIST;
    }
    
    public PlayerBanEvent(Player player, String reason) {
        this.player = player;
        this.reason = reason;
    }
}
