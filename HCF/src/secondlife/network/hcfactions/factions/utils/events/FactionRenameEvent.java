package secondlife.network.hcfactions.factions.utils.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.hcfactions.factions.Faction;

@Getter
@Setter
public class FactionRenameEvent extends FactionEvent implements Cancellable {

    private static HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private CommandSender sender;
    private String originalName;
    private String newName;

    public FactionRenameEvent(Faction faction, CommandSender sender, String originalName, String newName) {
        super(faction);
        this.sender = sender;
        this.originalName = originalName;
        this.newName = newName;
    }

   
    public void setNewName(String newName) {
        if (!newName.equals(this.newName)) {
            this.newName = newName;
        }
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
