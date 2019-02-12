package secondlife.network.hcfactions.factions.utils.events;

import org.bukkit.event.Event;

import com.google.common.base.Preconditions;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.hcfactions.factions.Faction;

@Getter
@Setter
public abstract class FactionEvent extends Event {

    protected Faction faction;

    public FactionEvent(Faction faction) {
        this.faction = Preconditions.checkNotNull(faction, "Faction cannot be null");
    }

    FactionEvent(Faction faction, boolean async) {
        super(async);
        this.faction = Preconditions.checkNotNull(faction, "Faction cannot be null");
    }
}