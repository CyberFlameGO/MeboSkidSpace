package secondlife.network.hcfactions.factions.utils.events;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.hcfactions.factions.type.PlayerFaction;

@Getter
@Setter
public class FactionPlayerJoinedEvent extends FactionEvent {

    private static HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private Player player;
    private CommandSender sender;
    private String playerUUID;

    public FactionPlayerJoinedEvent(CommandSender sender, Player player, String playerUUID, PlayerFaction playerFaction) {
        super(playerFaction);

        this.sender = sender;
        
        if(player != null) this.player = player;

        this.playerUUID = playerUUID;
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