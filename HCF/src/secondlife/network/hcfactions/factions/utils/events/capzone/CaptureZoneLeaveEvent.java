package secondlife.network.hcfactions.factions.utils.events.capzone;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.hcfactions.factions.utils.events.FactionEvent;
import secondlife.network.hcfactions.factions.type.games.CapturableFaction;
import secondlife.network.hcfactions.factions.utils.CaptureZone;

@Getter
@Setter
public class CaptureZoneLeaveEvent extends FactionEvent implements Cancellable {

    private static HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private CaptureZone captureZone;
    private Player player;

    public CaptureZoneLeaveEvent(Player player, CapturableFaction capturableFaction, CaptureZone captureZone) {
        super(capturableFaction);

        this.captureZone = captureZone;
        this.player = player;
    }

    @Override
    public CapturableFaction getFaction() {
        return (CapturableFaction) super.getFaction();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}