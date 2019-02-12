package secondlife.network.hcfactions.factions.utils.events;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.hcfactions.factions.utils.enums.ClaimChangeEnum;
import secondlife.network.hcfactions.factions.claim.ClaimZone;

@Getter
@Setter
public class FactionClaimChangedEvent extends Event {

    private static HandlerList handlers = new HandlerList();

    private CommandSender sender;
    private ClaimChangeEnum cause;
    private Collection<ClaimZone> affectedClaims;

    public FactionClaimChangedEvent(CommandSender sender, ClaimChangeEnum cause, Collection<ClaimZone> affectedClaims) {
        this.sender = sender;
        this.cause = cause;
        this.affectedClaims = affectedClaims;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}