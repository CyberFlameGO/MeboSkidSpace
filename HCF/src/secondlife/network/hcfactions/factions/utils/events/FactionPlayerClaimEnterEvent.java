package secondlife.network.hcfactions.factions.utils.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.hcfactions.factions.Faction;

@Getter
@Setter
public class FactionPlayerClaimEnterEvent extends Event implements Cancellable {

    private static HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private Player player;
    private Faction fromFaction;
    private Faction toFaction;
    private Location from;
    private Location to;
    private EnterCause enterCause;

    public FactionPlayerClaimEnterEvent(Player player, Location from, Location to, Faction fromFaction, Faction toFaction, EnterCause enterCause) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.fromFaction = fromFaction;
        this.toFaction = toFaction;
        this.enterCause = enterCause;
    }

    public enum EnterCause {
        TELEPORT, MOVEMENT
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}