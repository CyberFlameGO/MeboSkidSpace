package secondlife.network.hcfactions.factions.utils.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.hcfactions.factions.Faction;

@Getter
@Setter
public class FactionCreateEvent extends FactionEvent implements Cancellable {

    private static HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private CommandSender sender;

    public FactionCreateEvent(Faction faction, CommandSender sender) {
        super(faction);
        
        this.sender = sender;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
