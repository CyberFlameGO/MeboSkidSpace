package secondlife.network.hcfactions.classes.utils.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import lombok.Getter;
import secondlife.network.hcfactions.classes.utils.ArmorClass;

@Getter
public class ArmorClassUnequipEvent extends PlayerEvent {

	private static HandlerList handlers = new HandlerList();

	private ArmorClass armorClass;

	public ArmorClassUnequipEvent(Player player, ArmorClass armorClass) {
		super(player);

		this.armorClass = armorClass;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
