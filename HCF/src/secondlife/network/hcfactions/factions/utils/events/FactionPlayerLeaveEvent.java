package secondlife.network.hcfactions.factions.utils.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.google.common.base.Optional;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.hcfactions.factions.utils.enums.FactionLeaveEnum;
import secondlife.network.hcfactions.factions.type.PlayerFaction;

@Getter
@Setter
public class FactionPlayerLeaveEvent extends FactionEvent implements Cancellable {

	private static HandlerList handlers = new HandlerList();
    private CommandSender sender;
    private String uniqueID;
	private FactionLeaveEnum cause;
	private boolean isKick;
	private boolean force;
	private boolean cancelled;
	private Optional<Player> player;

	public FactionPlayerLeaveEvent(CommandSender sender, Player player, String playerUUID, PlayerFaction playerFaction, FactionLeaveEnum cause, boolean isKick, boolean force) {
		super(playerFaction);

		this.sender = sender;

		if(player != null) this.player = Optional.of(player);

		this.uniqueID = playerUUID;
		this.cause = cause;
		this.isKick = isKick;
		this.force = force;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Optional<Player> getPlayer() {
		if(this.player == null) this.player = Optional.fromNullable(Bukkit.getPlayer(this.uniqueID));

		return this.player;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public PlayerFaction getFaction() {
		return (PlayerFaction) super.getFaction();
	}
}
