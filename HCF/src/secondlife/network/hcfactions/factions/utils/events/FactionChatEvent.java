package secondlife.network.hcfactions.factions.utils.events;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.FactionMember;
import secondlife.network.hcfactions.factions.utils.struction.ChatChannel;

@Getter
@Setter
public class FactionChatEvent extends FactionEvent implements Cancellable {

    private static HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private Player player;
    private FactionMember factionMember;
    private ChatChannel chatChannel;
    private String message;
    private Collection<? extends CommandSender> recipients;
    private String fallbackFormat;

    public FactionChatEvent(boolean async, PlayerFaction faction, Player player, ChatChannel chatChannel, Collection<? extends CommandSender> recipients, String message) {
        super(faction, async);
        
        this.player = player;
        this.factionMember = faction.getMember(player.getName());
        this.chatChannel = chatChannel;
        this.recipients = recipients;
        this.message = message;
        this.fallbackFormat = chatChannel.getRawFormat(player);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}