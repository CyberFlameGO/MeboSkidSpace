package secondlife.network.hcfactions.factions.utils.events;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.hcfactions.factions.utils.enums.ClaimChangeEnum;
import secondlife.network.hcfactions.factions.type.ClaimableFaction;
import secondlife.network.hcfactions.factions.claim.ClaimZone;

@Getter
@Setter
public class FactionClaimChangeEvent extends Event implements Cancellable {

    private static HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private ClaimChangeEnum cause;
    private Collection<ClaimZone> affectedClaims;
    private ClaimableFaction claimableFaction;
    private CommandSender sender;

    public FactionClaimChangeEvent(CommandSender sender, ClaimChangeEnum cause, Collection<ClaimZone> affectedClaims, ClaimableFaction claimableFaction) {
        this.sender = sender;
        this.cause = cause;
        this.affectedClaims = ImmutableList.copyOf(affectedClaims);
        this.claimableFaction = claimableFaction;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}