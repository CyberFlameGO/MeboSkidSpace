package secondlife.network.hcfactions.factions.utils.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.google.common.base.Optional;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.hcfactions.factions.type.PlayerFaction;

@Getter
@Setter
public class FactionPlayerJoinEvent extends FactionEvent implements Cancellable {

    private static HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private Optional<Player> player; 
    private CommandSender sender;
    private String playerUUID;

    public FactionPlayerJoinEvent(CommandSender sender, Player player, String playerUUID, PlayerFaction playerFaction) {
        super(playerFaction);

        this.sender = sender;
        
        if(player != null)  this.player = Optional.of(player);

        this.playerUUID = playerUUID;
    }

    public Optional<Player> getPlayer() {
        if(this.player == null) this.player = Optional.fromNullable(Bukkit.getPlayer(this.playerUUID));

        return this.player;
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