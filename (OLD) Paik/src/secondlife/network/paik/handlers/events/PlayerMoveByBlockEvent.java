package secondlife.network.paik.handlers.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerMoveByBlockEvent extends Event {

	private Player player;
	private Location to;
	private Location from;
	
	private boolean cancelled;
	
	public PlayerMoveByBlockEvent(Player player, Location to, Location from) {
		this.player = player;
		this.to = to;
		this.from = from;
	}

	private static final HandlerList handlers = new HandlerList();
    
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
