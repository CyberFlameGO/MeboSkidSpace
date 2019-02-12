package secondlife.network.hcfactions.factions.utils.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.struction.Relation;

/**
 * Faction event called a {@link PlayerFaction} has removed a {@link Relation} with another {@link PlayerFaction}.
 */
public class FactionRelationRemoveEvent extends Event implements Cancellable {

    private static HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private PlayerFaction senderFaction;
    private PlayerFaction targetFaction;
    private Relation relation;

    public FactionRelationRemoveEvent(PlayerFaction senderFaction, PlayerFaction targetFaction, Relation relation) {
        this.senderFaction = senderFaction;
        this.targetFaction = targetFaction;
        this.relation = relation;
    }

    /**
     * Gets the {@link Faction} sending the request.
     *
     * @return the requesting {@link Faction}
     */
    public PlayerFaction getSenderFaction() {
        return senderFaction;
    }

    /**
     * Gets the {@link Faction} asked to accept request.
     *
     * @return the targeted {@link Faction}
     */
    public PlayerFaction getTargetFaction() {
        return targetFaction;
    }

    public Relation getRelation() {
        return relation;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}