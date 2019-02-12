package secondlife.network.overpass.utilties.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Marko on 28.03.2018.
 */

@Getter
@Setter
public class LoginEvent extends Event {

	public static HandlerList handlers = new HandlerList();
	public boolean cancelled = false;
	
	private Player player;
	
	public LoginEvent(Player player) {
		this.player = player;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
