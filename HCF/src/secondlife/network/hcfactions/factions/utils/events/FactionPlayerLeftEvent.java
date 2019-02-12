package secondlife.network.hcfactions.factions.utils.events;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.hcfactions.factions.utils.enums.FactionLeaveEnum;
import secondlife.network.hcfactions.factions.type.PlayerFaction;

@Getter
@Setter
public class FactionPlayerLeftEvent extends FactionEvent {

    private static HandlerList handlers = new HandlerList();

    private Player player;
    private CommandSender sender;
    private String uniqueID;
	private FactionLeaveEnum cause;
	private boolean isKick;
	private boolean force;

    public FactionPlayerLeftEvent(CommandSender sender, Player player, String playerUUID, PlayerFaction playerFaction, FactionLeaveEnum cause, boolean isKick, boolean force) {
        super(playerFaction);

        this.sender = sender;
        
        if(player != null) this.player = player;

        this.uniqueID = playerUUID;
        this.cause = cause;
        this.isKick = isKick;
        this.force = force;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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
