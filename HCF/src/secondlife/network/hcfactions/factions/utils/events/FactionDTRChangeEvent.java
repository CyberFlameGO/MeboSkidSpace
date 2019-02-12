package secondlife.network.hcfactions.factions.utils.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.hcfactions.factions.utils.struction.Raidable;

@Getter
@Setter
public class FactionDTRChangeEvent extends Event implements Cancellable {

    private static HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private DtrUpdateCause cause;
    private Raidable raidable;
    private double originalDtr;
    private double newDtr;

    public FactionDTRChangeEvent(DtrUpdateCause cause, Raidable raidable, double originalDtr, double newDtr) {
        this.cause = cause;
        this.raidable = raidable;
        this.originalDtr = originalDtr;
        this.newDtr = newDtr;
    }

    public enum DtrUpdateCause {
        REGENERATION, MEMBER_DEATH
    }

    @Override
    public boolean isCancelled() {
        return cancelled || (Math.abs(newDtr - originalDtr) == 0);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
